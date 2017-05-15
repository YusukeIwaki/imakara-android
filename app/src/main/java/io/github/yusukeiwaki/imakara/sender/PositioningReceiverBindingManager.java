package io.github.yusukeiwaki.imakara.sender;

import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.os.IBinder;

public class PositioningReceiverBindingManager {
    private final Context context;
    private boolean serviceIsBound;

    public interface OnStateChangedListener {
        void onPositioningReceiverStateChanged(boolean activated);
    }
    private OnStateChangedListener onStateChangedListener;

    public void setOnStateChangedListener(OnStateChangedListener onStateChangedListener) {
        this.onStateChangedListener = onStateChangedListener;
    }

    public PositioningReceiverBindingManager(Context context) {
        this.context = context;

        serviceIsBound = false;
    }

    public void start() {
        if (onStateChangedListener != null) onStateChangedListener.onPositioningReceiverStateChanged(serviceIsBound);
        if (!serviceIsBound) {
            context.bindService(PositioningRequestReceiverNotificationService.newIntent(context, true), connection, 0);
        }
    }

    public void stop() {
        if (serviceIsBound) {
            context.unbindService(connection);
            serviceIsBound = false;
        }
    }

    private final ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            serviceIsBound = true;
            if (onStateChangedListener != null) onStateChangedListener.onPositioningReceiverStateChanged(true);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            serviceIsBound = false;
            if (onStateChangedListener != null) onStateChangedListener.onPositioningReceiverStateChanged(false);
        }
    };
}
