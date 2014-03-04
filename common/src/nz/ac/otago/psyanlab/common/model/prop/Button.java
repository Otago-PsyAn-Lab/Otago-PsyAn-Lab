
package nz.ac.otago.psyanlab.common.model.prop;

import nz.ac.otago.psyanlab.common.R;
import nz.ac.otago.psyanlab.common.model.Prop;
import nz.ac.otago.psyanlab.common.model.util.EventId;
import nz.ac.otago.psyanlab.common.model.util.NameResolverFactory;

import android.content.Context;
import android.os.Parcel;

public class Button extends Text {
    private static final int EVENT_ON_CLICK = 0x01;

    private static final int EVENT_ON_LONG_CLICK = 0x02;

    public static NameResolverFactory getEventNameFactory() {
        return new EventNameFactory();
    }

    public static NameResolverFactory getMethodNameFactory() {
        return new MethodNameFactory();
    }

    public Button(Context context, Prop prop, int defaultSuffix) {
        super(context, prop, defaultSuffix);

        // if (TextUtils.isEmpty(name)
        // || TextUtils.equals(name,
        // context.getString(R.string.default_text_prop_name))) {
        // name = context.getString(R.string.default_button_prop_name);
        // }
    }

    public Button(Parcel in) {
        super(in);
    }

    @EventId(EVENT_ON_CLICK)
    public void onClickStub() {
    }

    @EventId(EVENT_ON_LONG_CLICK)
    public void onLongClickStub() {
    }

    protected static class EventNameFactory extends Text.EventNameFactory {
        @Override
        public int getResId(int lookup) {
            switch (lookup) {
                case EVENT_ON_CLICK:
                    return R.string.event_on_click;
                case EVENT_ON_LONG_CLICK:
                    return R.string.event_on_long_click;

                default:
                    return R.string.event_missing_string;
            }
        }
    }

    protected static class MethodNameFactory extends Text.MethodNameFactory {
        @Override
        public int getResId(int lookup) {
            switch (lookup) {
                default:
                    return super.getResId(lookup);
            }
        }
    }
}
