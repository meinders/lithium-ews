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

package lithium.io.rtf;

/**
 * Text contained in an RTF document.
 *
 * @author Gerrit Meinders
 */
public class TextNode
        extends AbstractRtfNode {
    private String _text;

    /**
     * Constructs a new instance.
     */
    public TextNode() {
    }

    public TextNode(String text) {
        setText(text);
    }

    public String getText() {
        return _text;
    }

    public void setText(String text) {
        _text = text;
    }

    @Override
    public void accept(RtfVisitor visitor) {
        visitor.visitText(this);
    }

    @Override
    public String toString()
    {
        return super.toString() + "[text=" + _text+ "]";
    }
}
