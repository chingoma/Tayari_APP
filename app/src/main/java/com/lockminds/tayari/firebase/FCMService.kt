package com.lockminds.tayari.firebase

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.ParsedRequestListener
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.reflect.TypeToken
import com.lockminds.tayari.*
import com.lockminds.tayari.constants.Constants
import com.lockminds.tayari.constants.APIURLs
import com.lockminds.tayari.responses.Response
import com.lockminds.tayari.worker.AppWorker

class FCMService : FirebaseMessagingService() {

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        // [START_EXCLUDE]
        // There are two types of messages data messages and notification messages. Data messages are handled
        // here in onMessageReceived whether the app is in the foreground or background. Data messages are the type
        // traditionally used with GCM. Notification messages are only received here in onMessageReceived when the app
        // is in the foreground. When the app is in the background an automatically generated notification is displayed.
        // When the user taps on the notification they are returned to the app. Messages containing both notification
        // and data payloads are treated as notification messages. The Firebase console always sends notification
        // messages. For more see: https://firebase.google.com/docs/cloud-messaging/concept-options
        // [END_EXCLUDE]

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: ${remoteMessage.from}")

        // Check if message contains a data payload.
        if (remoteMessage.data.isNotEmpty()) {

            Log.d(TAG, "Message data payload: ${remoteMessage.data}")

            if (remoteMessage.data["worker"].equals("true")) {
                // For long-running tasks (10 seconds or more) use WorkManager.
                scheduleJob(remoteMessage)
            } else {
                // Handle message within 10 seconds
                handleNow(remoteMessage)
            }
        }

        // Check if message contains a notification payload.
        remoteMessage.notification?.let {
            Log.d(TAG, "Message Notification Body: ${it.body}")
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }
    // [END receive_message]

    // [START on_new_token]
    /**
     * Called if the FCM registration token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the
     * FCM registration token is initially generated so this is where you would retrieve the token.
     */
    override fun onNewToken(token: String) {
        Log.d(TAG, "Refreshed token: $token")

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // FCM registration token to your app server.
        sendRegistrationToServer(token)
    }
    // [END on_new_token]

    /**
     * Schedule async work using WorkManager.
     */
    private fun scheduleJob(remoteMessage: RemoteMessage) {
        // [START dispatch_job]
        val work = OneTimeWorkRequest.Builder(AppWorker::class.java).build()
        WorkManager.getInstance().beginWith(work).enqueue()
        // [END dispatch_job]
    }

    /**
     * Handle time allotted to BroadcastReceivers.
     */
    private fun handleNow(remoteMessage: RemoteMessage) {
        sendNotification(remoteMessage)
    }

    /**
     * Persist token to third-party servers.
     *
     * Modify this method to associate the user's FCM registration token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private fun sendRegistrationToServer(token: String?) {
        val preference = applicationContext?.getSharedPreferences(
                Constants.PREFERENCE_KEY,
                Context.MODE_PRIVATE
        )
                ?: return

        with(preference.edit()) {
            putString(Constants.FCM_TOKEN, token)
            apply()
        }

        if (preference != null) {
            AndroidNetworking.post(APIURLs.BASE_URL + "user/update_fcm_token")
                .addBodyParameter("fcm_token", token)
                .addHeaders("accept", "application/json")
                .setPriority(Priority.HIGH)
                .addHeaders("Authorization", "Bearer " + preference.getString(Constants.LOGIN_TOKEN, "false"))
                .build()
                .getAsParsed(
                    object : TypeToken<Response?>() {},
                    object : ParsedRequestListener<Response> {

                        override fun onResponse(response: Response) {
                            if (response.status) {

                            } else {

                            }

                        }

                        override fun onError(anError: ANError) { }

                    })
        }
    }

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param messageBody FCM message body received.
     */
    private fun sendNotification(remoteMessage: RemoteMessage) {
        val tools = Tools()
        val type = remoteMessage.data["type"]
        val intent = if(type.equals("order")){
            Intent(this, OrdersActivity::class.java)
        }else if(type.equals("order_paid")){
            Intent(this, OrdersActivity::class.java)
        }else{
            Intent(this, MainActivity::class.java)
        }

        if(type.equals("order_paid")) {
            val intents = Intent(this@FCMService, OrdersActivity::class.java)
            intents.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intents)
        }
            val icon = BitmapFactory.decodeResource(resources,R.drawable.ic_notification_icon)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            val pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT)

            val  message = remoteMessage.data["message"]?.replace(remoteMessage.data["old"].toString(),remoteMessage.data["new"].toString())
            val channelId = getString(R.string.default_notification_channel_id)
            val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val notificationBuilder = NotificationCompat.Builder(this, channelId)
                .setLargeIcon(icon)
                .setSmallIcon(R.drawable.ic_notification_icon)
                .setContentTitle(tools.fromHtml(remoteMessage.data["title"]))
                .setContentText(tools.fromHtml(message))
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)

            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            // Since android Oreo notification channel is needed.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(channelId,
                    getString(R.string.default_notification_channel_id_desc),
                    NotificationManager.IMPORTANCE_HIGH)
                notificationManager.createNotificationChannel(channel)
            }

            notificationManager.notify(0 /* ID of notification */, notificationBuilder.build())
        }


    companion object {
        private const val TAG = "MyFirebaseMsgService"
    }

    private fun String.replace(oldChar: Char, newChar: Char, ignoreCase: Boolean = false): String {
        return this.replace(oldChar, newChar)
    }

}