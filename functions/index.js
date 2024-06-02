/**
 * Import function triggers from their respective submodules:
 *
 * const {onCall} = require("firebase-functions/v2/https");
 * const {onDocumentWritten} = require("firebase-functions/v2/firestore");
 *
 * See a full list of supported triggers at https://firebase.google.com/docs/functions
 */

const functions = require("firebase-functions");
const admin = require("firebase-admin");
admin.initializeApp();

exports.sendSubscriptionReminder = functions.pubsub
    .schedule("every 24 hours")
    .onRun(async (context) => {
      const db = admin.firestore();
      const today = new Date();
      const tomorrow = new Date(today);
      tomorrow.setDate(today.getDate() + 1);
      const tomorrowDate = tomorrow.getDate();

      const subscriptionsSnapshot = await db.collection("subscriptions")
          .where("paymentDate", "==", tomorrowDate)
          .get();

      const tokensWithNames = [];
      subscriptionsSnapshot.forEach((doc) => {
        const subscription = doc.data();
        if (subscription.fcmToken && subscription.name) {
          tokensWithNames.push({
            fcmToken: subscription.fcmToken,
            subscriptionName: subscription.name,
          });
        }
      });

      if (tokensWithNames.length > 0) {
        const messages = tokensWithNames.map((tokenWithName) => ({
          notification: {
            title: "Subscription Reminder",
            body: `Your ${tokenWithName.subscriptionName}` +
            ` subscription payment is due tomorrow.`,
          },
          token: tokenWithName.fcmToken,
        }));

        const sendPromises = messages.map((message) =>
          admin.messaging().send(message),
        );

        Promise.all(sendPromises)
            .then((responses) => {
              console.log(responses.length + " messages were" +
              " sent successfully");
            })
            .catch((error) => {
              console.error("Error sending messages:", error);
            });
      }

      return null;
    });
