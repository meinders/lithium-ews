/*
 * Copyright 2013 Gerrit Meinders
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

/**
 * Binary content, e.g. audio or video.
 *
 * @author Gerrit Meinders
 */
public class BinaryContent
        implements Content {
    private byte[] _bytes;
    private boolean precededByZeros = true;

    /**
     * Constructs a new instance.
     */
    public BinaryContent() {
    }

    public BinaryContent(final byte[] bytes) {
        setBytes(bytes);
    }

    public void setBytes(final byte[] bytes) {
        _bytes = bytes;
    }

    public byte[] getBytes() {
        return _bytes;
    }

    @Override
    public String toString() {
        return super.toString() + "[length=" + _bytes.length + "]";
    }

    public boolean isPrecededByZeros()
    {
        return precededByZeros;
    }

    public void setPrecededByZeros( final boolean precededByZeros )
    {
        this.precededByZeros = precededByZeros;
    }
}
