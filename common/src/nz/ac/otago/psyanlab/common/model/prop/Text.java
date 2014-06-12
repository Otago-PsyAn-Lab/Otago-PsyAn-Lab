
package nz.ac.otago.psyanlab.common.model.prop;

import com.google.gson.annotations.Expose;

import nz.ac.otago.psyanlab.common.R;
import nz.ac.otago.psyanlab.common.model.Prop;
import nz.ac.otago.psyanlab.common.model.util.MethodId;
import nz.ac.otago.psyanlab.common.model.util.NameResolverFactory;
import nz.ac.otago.psyanlab.common.model.util.PALEPropProperty;
import nz.ac.otago.psyanlab.common.model.util.ParameterId;

import android.content.Context;
import android.os.Parcel;

public class Text extends Prop {
    protected static final int METHOD_GET_CHARACTER_AT_POSITION = 0x01 + Prop.TEXT_NS_OFFSET;

    protected static final int METHOD_GET_CHARACTER_AT_X_POSITION = 0x02 + Prop.TEXT_NS_OFFSET;

    protected static final int METHOD_GET_CHARACTER_INDEX_AT_POSITION = 0x03 + Prop.TEXT_NS_OFFSET;

    protected static final int METHOD_GET_CHARACTER_INDEX_AT_X_POSITION = 0x04 + Prop.TEXT_NS_OFFSET;

    protected static final int METHOD_GET_TEXT = 0x05 + Prop.TEXT_NS_OFFSET;

    protected static final int METHOD_GET_TEXT_SIZE = 0x06 + Prop.TEXT_NS_OFFSET;

    protected static final int METHOD_GET_WORD_AT_POSITION = 0x07 + Prop.TEXT_NS_OFFSET;

    protected static final int METHOD_GET_WORD_AT_X_POSITION = 0x08 + Prop.TEXT_NS_OFFSET;

    protected static final int METHOD_GET_WORD_INDEX_AT_POSITION = 0x09 + Prop.TEXT_NS_OFFSET;

    protected static final int METHOD_GET_WORD_INDEX_AT_X_POSITION = 0x0a + Prop.TEXT_NS_OFFSET;

    protected static final int METHOD_IS_POSITION_ABOVE_TEXT = 0x0b + Prop.TEXT_NS_OFFSET;

    protected static final int METHOD_IS_POSITION_BELOW_TEXT = 0x0c + Prop.TEXT_NS_OFFSET;

    protected static final int METHOD_IS_POSITION_TO_LEFT_OF_TEXT = 0x0d + Prop.TEXT_NS_OFFSET;

    protected static final int METHOD_IS_POSITION_TO_RIGHT_OF_TEXT = 0x0e + Prop.TEXT_NS_OFFSET;

    protected static final int METHOD_SET_FONT = 0x0f + Prop.TEXT_NS_OFFSET;

    protected static final int METHOD_SET_FONT_COLOUR = 0x10 + Prop.TEXT_NS_OFFSET;

    protected static final int METHOD_SET_FONT_COLOUR_HTML = 0x11 + Prop.TEXT_NS_OFFSET;

    protected static final int METHOD_SET_FONT_COLOUR_RGB = 0x12 + Prop.TEXT_NS_OFFSET;

    protected static final int METHOD_SET_TEXT = 0x13 + Prop.TEXT_NS_OFFSET;

    protected static final int METHOD_SET_TEXT_SIZE = 0x14 + Prop.TEXT_NS_OFFSET;

    protected static final int PARAM_BLUE = 0x01 + Prop.TEXT_NS_OFFSET;

    protected static final int PARAM_COLOUR = 0x02 + Prop.TEXT_NS_OFFSET;

    protected static final int PARAM_COLOUR_HTML = 0x03 + Prop.TEXT_NS_OFFSET;

    protected static final int PARAM_GREEN = 0x04 + Prop.TEXT_NS_OFFSET;

    protected static final int PARAM_RED = 0x05 + Prop.TEXT_NS_OFFSET;

    protected static final int PARAM_TEXT = 0x06 + Prop.TEXT_NS_OFFSET;

    protected static final int PARAM_TEXT_SIZE = 0x07 + Prop.TEXT_NS_OFFSET;

    public static NameResolverFactory getEventNameFactory() {
        return new EventNameFactory();
    }

    @Expose
    @PALEPropProperty(value = "Font Size")
    public int fontSize = -1;

    @Expose
    @PALEPropProperty(value = "String")
    public String text;

    public Text(Context context, Prop prop, int defaultSuffix) {
        super(context, prop, defaultSuffix);

        fontSize = context.getResources().getDimensionPixelSize(R.dimen.default_text_size);

        if (prop == null) {
            return;
        }

        if (prop instanceof Text) {
            Text old = (Text)prop;
            text = old.text;
            fontSize = old.fontSize;
        }

    }

    public Text(Parcel in) {
        super(in);

        text = in.readString();
        fontSize = in.readInt();
    }

    @Override
    public NameResolverFactory getMethodNameFactory() {
        return new MethodNameFactory();
    }

    @Override
    public NameResolverFactory getParameterNameFactory() {
        return new ParameterNameFactory();
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @MethodId(METHOD_GET_CHARACTER_AT_POSITION)
    public String stubGetCharacterAtPosition(@ParameterId(PARAM_X_COORDINATE) int x,
            @ParameterId(PARAM_Y_COORDINATE) int y) {
        return null;
    }

    @MethodId(METHOD_GET_CHARACTER_AT_X_POSITION)
    public String stubGetCharacterAtXPosition(@ParameterId(PARAM_X_COORDINATE) int x) {
        return null;
    }

    @MethodId(METHOD_GET_CHARACTER_INDEX_AT_POSITION)
    public int stubGetCharacterIndexAtPosition(@ParameterId(PARAM_X_COORDINATE) int x,
            @ParameterId(PARAM_Y_COORDINATE) int y) {
        return 0;
    }

    @MethodId(METHOD_GET_CHARACTER_INDEX_AT_X_POSITION)
    public int stubGetCharacterIndexAtXPosition(@ParameterId(PARAM_X_COORDINATE) int x) {
        return 0;
    }

    @MethodId(METHOD_GET_TEXT)
    public String stubGetText() {
        return null;
    }

    @MethodId(METHOD_GET_TEXT_SIZE)
    public int stubGetTextSize() {
        return 0;
    }

    @MethodId(METHOD_GET_WORD_AT_POSITION)
    public String stubGetWordAtPosition(@ParameterId(PARAM_X_COORDINATE) int x,
            @ParameterId(PARAM_Y_COORDINATE) int y) {
        return null;
    }

    @MethodId(METHOD_GET_WORD_AT_X_POSITION)
    public String stubGetWordAtXPosition(@ParameterId(PARAM_X_COORDINATE) int x) {
        return null;
    }

    @MethodId(METHOD_GET_WORD_INDEX_AT_POSITION)
    public int stubGetWordIndexAtPosition(@ParameterId(PARAM_X_COORDINATE) int x,
            @ParameterId(PARAM_Y_COORDINATE) int y) {
        return 0;
    }

    @MethodId(METHOD_GET_WORD_INDEX_AT_X_POSITION)
    public int stubGetWordIndexAtXPosition(@ParameterId(PARAM_X_COORDINATE) int x) {
        return 0;
    }

    @MethodId(METHOD_IS_POSITION_ABOVE_TEXT)
    public boolean stubIsPositionAboveText(@ParameterId(PARAM_Y_COORDINATE) int y) {
        return false;
    }

    @MethodId(METHOD_IS_POSITION_BELOW_TEXT)
    public boolean stubIsPositionBelowText(@ParameterId(PARAM_Y_COORDINATE) int y) {
        return false;
    }

    @MethodId(METHOD_IS_POSITION_TO_LEFT_OF_TEXT)
    public boolean stubIsPositionToLeftOfText(@ParameterId(PARAM_X_COORDINATE) int x) {
        return false;
    }

    @MethodId(METHOD_IS_POSITION_TO_RIGHT_OF_TEXT)
    public boolean stubIsPositionToRightOfText(@ParameterId(PARAM_X_COORDINATE) int x) {
        return false;
    }

    @MethodId(METHOD_SET_FONT)
    public void stubSetFont() {
    }

    @MethodId(METHOD_SET_FONT_COLOUR)
    public void stubSetFontColour(@ParameterId(PARAM_COLOUR) int colour) {
    }

    @MethodId(METHOD_SET_FONT_COLOUR_HTML)
    public void stubSetFontColourHTML(@ParameterId(PARAM_COLOUR_HTML) String colour) {
    }

    @MethodId(METHOD_SET_FONT_COLOUR_RGB)
    public void stubSetFontColourRGB(@ParameterId(PARAM_RED) int red,
            @ParameterId(PARAM_GREEN) int green, @ParameterId(PARAM_BLUE) int blue) {
    }

    @MethodId(METHOD_SET_TEXT)
    public void stubSetText(@ParameterId(PARAM_TEXT) String text) {
    }

    @MethodId(METHOD_SET_TEXT_SIZE)
    public void stubSetTextSize(@ParameterId(PARAM_TEXT_SIZE) int size) {
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);

        dest.writeString(text);
        dest.writeInt(fontSize);
    }

    protected static class EventNameFactory extends Prop.EventNameFactory {
        @Override
        public int getResId(int lookup) {
            switch (lookup) {
                default:
                    return super.getResId(lookup);
            }
        }
    }

    protected static class MethodNameFactory extends Prop.MethodNameFactory {

        @Override
        public int getResId(int lookup) {
            switch (lookup) {
                case METHOD_SET_TEXT:
                    return R.string.method_set_text;
                case METHOD_GET_TEXT:
                    return R.string.method_get_text;
                case METHOD_SET_TEXT_SIZE:
                    return R.string.method_set_text_size;
                case METHOD_GET_TEXT_SIZE:
                    return R.string.method_get_text_size;
                case METHOD_SET_FONT:
                    return R.string.method_set_font;
                case METHOD_SET_FONT_COLOUR_HTML:
                    return R.string.method_set_colour_font_html;
                case METHOD_SET_FONT_COLOUR_RGB:
                    return R.string.method_set_font_colour_rgb;
                case METHOD_SET_FONT_COLOUR:
                    return R.string.method_set_font_colour;
                case METHOD_IS_POSITION_ABOVE_TEXT:
                    return R.string.method_is_position_above_text;
                case METHOD_IS_POSITION_BELOW_TEXT:
                    return R.string.method_is_position_below_text;
                case METHOD_IS_POSITION_TO_LEFT_OF_TEXT:
                    return R.string.method_is_position_to_left_of_text;
                case METHOD_IS_POSITION_TO_RIGHT_OF_TEXT:
                    return R.string.method_is_position_to_right_of_text;
                case METHOD_GET_CHARACTER_AT_POSITION:
                    return R.string.method_get_character_at_position;
                case METHOD_GET_CHARACTER_AT_X_POSITION:
                    return R.string.method_get_character_at_x_position;
                case METHOD_GET_CHARACTER_INDEX_AT_POSITION:
                    return R.string.method_get_character_index_at_position;
                case METHOD_GET_CHARACTER_INDEX_AT_X_POSITION:
                    return R.string.method_get_character_index_at_x_position;
                case METHOD_GET_WORD_AT_POSITION:
                    return R.string.method_get_word_at_position;
                case METHOD_GET_WORD_AT_X_POSITION:
                    return R.string.method_get_word_at_x_position;
                case METHOD_GET_WORD_INDEX_AT_POSITION:
                    return R.string.method_get_word_index_at_position;
                case METHOD_GET_WORD_INDEX_AT_X_POSITION:
                    return R.string.method_get_word_index_at_x_position;

                default:
                    return super.getResId(lookup);
            }
        }
    }

    protected static class ParameterNameFactory extends Prop.ParameterNameFactory {
        @Override
        public int getResId(int lookup) {
            switch (lookup) {
                case PARAM_COLOUR_HTML:
                    return R.string.parameter_html_colour_code;
                case PARAM_TEXT:
                    return R.string.parameter_text;
                case PARAM_RED:
                    return R.string.parameter_red;
                case PARAM_GREEN:
                    return R.string.parameter_green;
                case PARAM_BLUE:
                    return R.string.parameter_blue;
                case PARAM_COLOUR:
                    return R.string.paramter_colour;
                case PARAM_TEXT_SIZE:
                    return R.string.parameter_size;

                default:
                    return super.getResId(lookup);
            }
        }
    }
}
