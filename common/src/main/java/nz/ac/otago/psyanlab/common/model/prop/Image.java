/*
 Copyright (C) 2012, 2013, 2014 University of Otago, Tonic Artos <tonic.artos@gmail.com>

 Otago PsyAn Lab is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program. If not, see <http://www.gnu.org/licenses/>.

 In accordance with Section 7(b) of the GNU General Public License version 3,
 all legal notices and author attributions must be preserved.
 */

package nz.ac.otago.psyanlab.common.model.prop;

import nz.ac.otago.psyanlab.common.R;
import nz.ac.otago.psyanlab.common.model.Prop;
import nz.ac.otago.psyanlab.common.model.typestub.ImageStub;
import nz.ac.otago.psyanlab.common.model.util.MethodId;
import nz.ac.otago.psyanlab.common.model.util.NameResolverFactory;
import nz.ac.otago.psyanlab.common.model.util.ParameterId;

import android.content.Context;
import android.os.Parcel;

public class Image extends Prop {
    protected static final int METHOD_GET_IMAGE = 0x01 + Prop.IMAGE_NS_OFFSET;

    protected static final int METHOD_SET_IMAGE = 0x02 + Prop.IMAGE_NS_OFFSET;

    protected static final int PARAM_IMAGE = 0x01 + Prop.IMAGE_NS_OFFSET;

    public Image(Context context, Prop prop) {
        super(context, prop);
    }

    public Image(Parcel in) {
        super(in);
    }

    public static NameResolverFactory getEventNameFactory() {
        return new EventNameFactory();
    }

    @Override
    public NameResolverFactory getMethodNameFactory() {
        return new MethodNameFactory();
    }

    @Override
    public NameResolverFactory getParameterNameFactory() {
        return new ParameterNameFactory();
    }

    @MethodId(METHOD_GET_IMAGE)
    public ImageStub stubGetImage() {
        return null;
    }

    @MethodId(METHOD_SET_IMAGE)
    public void stubSetImage(@ParameterId(PARAM_IMAGE) ImageStub image) {
    }

    protected static class EventNameFactory extends Prop.EventNameFactory {
        @Override
        public String getName(Context context, int lookup) {
            switch (lookup) {
                default:
                    return super.getName(context, lookup);
            }
        }
    }

    protected static class MethodNameFactory extends Prop.MethodNameFactory {
        @Override
        public String getName(Context context, int lookup) {
            switch (lookup) {
                case METHOD_GET_IMAGE:
                    return context.getString(R.string.method_get_image);
                case METHOD_SET_IMAGE:
                    return context.getString(R.string.method_set_image);
                default:
                    return super.getName(context, lookup);
            }
        }
    }

    protected static class ParameterNameFactory extends Prop.ParameterNameFactory {
        @Override
        public String getName(Context context, int lookup) {
            switch (lookup) {
                case PARAM_IMAGE:
                    return context.getString(R.string.parameter_image);
                default:
                    return super.getName(context, lookup);
            }
        }
    }
}
