/*
 * Copyright (c) 2012, 2013 University of Otago, Tonic Artos <tonic.artos@gmail.com>
 * 
 * Otago PsyAn Lab is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 * 
 * In accordance with Section 7(b) of the GNU General Public License version 3,
 * all legal notices and author attributions must be preserved.
 */

package nz.ac.otago.psyanlab.common.model.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Encoding of an event id to identify an event and its implementation.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface EventData {
    public static final int EVENT_NO_OBJECT = 0x00;

    public static final int EVENT_SCENE_FINISH = 0x004;

    public static final int EVENT_SCENE_START = 0x03;

    public static final int EVENT_TOUCH = 0x01;

    public static final int EVENT_TOUCH_MOTION = 0x02;

    int id();

    int type();
}
