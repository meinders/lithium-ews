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
 * Background image.
 *
 * @author Gerrit Meinders
 */
public class ImageBackground
        implements Background {
    private String _name = "";

    private BinaryContent _image;

    private boolean _tiled = false;

    private ScheduleEntry.AspectRatio _aspectRatio = ScheduleEntry.AspectRatio.STRETCH;

    /**
     * Constructs a new instance.
     */
    public ImageBackground() {
    }

    public ImageBackground(String name, final byte[] bytes) {
        setName(name);
        setImage(new BinaryContent(bytes));
    }

    public ImageBackground(String name, BinaryContent image) {
        setName(name);
        setImage(image);
    }

    public void setName(String name) {
        _name = name;
    }

    public String getName() {
        return _name;
    }

    public void setImage(BinaryContent image) {
        _image = image;
    }

    public BinaryContent getImage() {
        return _image;
    }

    public void setTiled(boolean tiled) {
        _tiled = tiled;
    }

    public boolean isTiled() {
        return _tiled;
    }

    public void setAspectRatio(ScheduleEntry.AspectRatio aspectRatio) {
        _aspectRatio = aspectRatio;
    }

    public ScheduleEntry.AspectRatio getAspectRatio() {
        return _aspectRatio;
    }

    @Override
    public String toString() {
        return super.toString() + "[name=" + _name + ",tiled=" + _tiled + ",aspectRatio=" + _aspectRatio + "]";
    }
}
