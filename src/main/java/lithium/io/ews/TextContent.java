/*
 * Copyright 2013-2014 Gerrit Meinders
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package lithium.io.ews;

import lithium.io.rtf.RtfGroup;

/**
 * Text content, e.g. songs or scripture, typically with RTF markup.
 *
 * @author Gerrit Meinders
 */
public class TextContent
        implements Content {
    private RtfGroup _text;

    /**
     * Constructs a new instance.
     */
    public TextContent() {
    }

    public TextContent(final RtfGroup text) {
        setText(text);
    }

    public RtfGroup getText() {
        return _text;
    }

    public void setText(final RtfGroup text) {
        _text = text;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
