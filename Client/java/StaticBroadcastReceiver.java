package aki.chat;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

/**
 * Created by Shadow on 2016/12/14.
 */
public class StaticBroadcastReceiver extends BroadcastReceiver {
//    public static void sendNotification(Context context, String contentTitle, String contentText, String ticker) {
//        Intent intent = new Intent("StaticBroadcast");
//        intent.putExtra("ContentTitle", contentTitle);
//        intent.putExtra("ContentText", contentText);
//        intent.putExtra("Ticker", ticker);
//        context.sendBroadcast(intent);
//    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent newIntent = new Intent(context, MainActivity.class);
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.logo_mini);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = new Notification.Builder(context)
                .setContentTitle(intent.getExtras().getString("ContentTitle"))
                .setContentText(intent.getExtras().getString("ContentText"))
                .setTicker(intent.getExtras().getString("Ticker"))
                .setLargeIcon(bitmap)
                .setSmallIcon(R.drawable.logo_mini)
                .setAutoCancel(true)
                .setContentIntent(PendingIntent.getActivity(context, 0, newIntent, 0))
                .build();

        notificationManager.notify(0, notification);
    }
}
