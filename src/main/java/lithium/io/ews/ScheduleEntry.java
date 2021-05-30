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

import java.awt.*;
import java.util.Date;

/**
 * An entry in a {@link Schedule}, which provides content and various meta
 * information.
 *
 * @author Gerrit Meinders
 */
public class ScheduleEntry {
    private String _title;

    private String _mediaResource;

    private String _author;

    private String _copyright;

    private String _administrator;

    private Date _timestamp;

    private Content _content;

    private Type _type;

    private String _notes;

    private String _songNumber;

    private Presentation _presentation;

    private Background _background;

    private BinaryContent _thumbnailImage;

    private boolean customFontSettings = false;
    private boolean fontSizeAutomatic = true;
    private int fontSize = 0;
    private boolean useDefaultFont = true;
    private String fontName = "";
    private boolean foregroundAutomatic = true;
    private Color foregroundColor = null;
    private boolean shadowAutomatic = true;
    private Color shadowColor = null;
    private boolean outlineAutomatic = true;
    private Color outlineColor = null;
    private Boolean shadowEnabled = null;
    private Boolean outlineEnabled = null;
    private Boolean boldEnabled = null;
    private Boolean italicEnabled = null;
    private HorizontalAlignment horizontalTextAlignment = HorizontalAlignment.DEFAULT;
    private VerticalAlignment verticalTextAlignment = VerticalAlignment.DEFAULT;
    private boolean defaultTextMargins = true;
    private int textMarginLeft = 0;
    private int textMarginTop = 0;
    private int textMarginRight = 0;
    private int textMarginBottom = 0;

    /**
     * Constructs a new instance.
     */
    public ScheduleEntry() {
    }

    public ScheduleEntry(final String title) {
        setTitle(title);
    }

    public ScheduleEntry(final String title, final Content content) {
        setTitle(title);
        setContent(content);
    }

    public String getTitle() {
        return _title;
    }

    public void setTitle(final String title) {
        _title = title;
    }

    public String getMediaResource() {
        return _mediaResource;
    }

    public void setMediaResource(final String mediaResource) {
        _mediaResource = mediaResource;
    }

    public String getAuthor() {
        return _author;
    }

    public void setAuthor(final String author) {
        _author = author;
    }

    public String getCopyright() {
        return _copyright;
    }

    public void setCopyright(final String copyright) {
        _copyright = copyright;
    }

    public String getAdministrator() {
        return _administrator;
    }

    public void setAdministrator(final String administrator) {
        _administrator = administrator;
    }

    public Date getTimestamp() {
        return _timestamp;
    }

    public void setTimestamp(final Date timestamp) {
        _timestamp = timestamp;
    }

    public Content getContent() {
        return _content;
    }

    public void setContent(final Content content) {
        _content = content;
    }

    public Type getType() {
        return _type;
    }

    public void setType(final Type type) {
        _type = type;
    }

    public String getNotes() {
        return _notes;
    }

    public void setNotes(final String notes) {
        _notes = notes;
    }

    public String getSongNumber() {
        return _songNumber;
    }

    public void setSongNumber(final String songNumber) {
        _songNumber = songNumber;
    }

    public void setPresentation(final Presentation presentation) {
        _presentation = presentation;
    }

    public Presentation getPresentation() {
        return _presentation;
    }

    public void setBackground(final Background background) {
        _background = background;
    }

    public Background getBackground() {
        return _background;
    }

    public void setThumbnailImage(BinaryContent thumbnailImage) {
        _thumbnailImage = thumbnailImage;
    }

    public BinaryContent getThumbnailImage() {
        return _thumbnailImage;
    }

    public boolean isCustomFontSettings() {
        return customFontSettings;
    }

    public void setCustomFontSettings(boolean customFontSettings) {
        this.customFontSettings = customFontSettings;
    }

    public boolean isFontSizeAutomatic() {
        return fontSizeAutomatic;
    }

    public void setFontSizeAutomatic(boolean fontSizeAutomatic) {
        this.fontSizeAutomatic = fontSizeAutomatic;
    }

    public int getFontSize() {
        return fontSize;
    }

    public void setFontSize(int fontSize) {
        this.fontSize = fontSize;
    }

    public boolean isUseDefaultFont() {
        return useDefaultFont;
    }

    public void setUseDefaultFont(boolean useDefaultFont) {
        this.useDefaultFont = useDefaultFont;
    }

    public String getFontName() {
        return fontName;
    }

    public void setFontName(String fontName) {
        this.fontName = fontName;
    }

    public boolean isForegroundAutomatic() {
        return foregroundAutomatic;
    }

    public void setForegroundAutomatic(boolean foregroundAutomatic) {
        this.foregroundAutomatic = foregroundAutomatic;
    }

    public Color getForegroundColor() {
        return foregroundColor;
    }

    public void setForegroundColor(Color foregroundColor) {
        this.foregroundColor = foregroundColor;
    }

    public boolean isShadowAutomatic() {
        return shadowAutomatic;
    }

    public void setShadowAutomatic(boolean shadowAutomatic) {
        this.shadowAutomatic = shadowAutomatic;
    }

    public Color getShadowColor() {
        return shadowColor;
    }

    public void setShadowColor(Color shadowColor) {
        this.shadowColor = shadowColor;
    }

    public boolean isOutlineAutomatic() {
        return outlineAutomatic;
    }

    public void setOutlineAutomatic(boolean outlineAutomatic) {
        this.outlineAutomatic = outlineAutomatic;
    }

    public Color getOutlineColor() {
        return outlineColor;
    }

    public void setOutlineColor(Color outlineColor) {
        this.outlineColor = outlineColor;
    }

    public Boolean getShadowEnabled() {
        return shadowEnabled;
    }

    public void setShadowEnabled(Boolean shadowEnabled) {
        this.shadowEnabled = shadowEnabled;
    }

    public Boolean getOutlineEnabled() {
        return outlineEnabled;
    }

    public void setOutlineEnabled(Boolean outlineEnabled) {
        this.outlineEnabled = outlineEnabled;
    }

    public Boolean getBoldEnabled() {
        return boldEnabled;
    }

    public void setBoldEnabled(Boolean boldEnabled) {
        this.boldEnabled = boldEnabled;
    }

    public Boolean getItalicEnabled() {
        return italicEnabled;
    }

    public void setItalicEnabled(Boolean italicEnabled) {
        this.italicEnabled = italicEnabled;
    }

    public HorizontalAlignment getHorizontalTextAlignment() {
        return horizontalTextAlignment;
    }

    public void setHorizontalTextAlignment(HorizontalAlignment horizontalTextAlignment) {
        this.horizontalTextAlignment = horizontalTextAlignment;
    }

    public VerticalAlignment getVerticalTextAlignment() {
        return verticalTextAlignment;
    }

    public void setVerticalTextAlignment(VerticalAlignment verticalTextAlignment) {
        this.verticalTextAlignment = verticalTextAlignment;
    }

    public boolean isDefaultTextMargins() {
        return defaultTextMargins;
    }

    public void setDefaultTextMargins(boolean defaultTextMargins) {
        this.defaultTextMargins = defaultTextMargins;
    }

    public int getTextMarginLeft() {
        return textMarginLeft;
    }

    public void setTextMarginLeft(int textMarginLeft) {
        this.textMarginLeft = textMarginLeft;
    }

    public int getTextMarginTop() {
        return textMarginTop;
    }

    public void setTextMarginTop(int textMarginTop) {
        this.textMarginTop = textMarginTop;
    }

    public int getTextMarginRight() {
        return textMarginRight;
    }

    public void setTextMarginRight(int textMarginRight) {
        this.textMarginRight = textMarginRight;
    }

    public int getTextMarginBottom() {
        return textMarginBottom;
    }

    public void setTextMarginBottom(int textMarginBottom) {
        this.textMarginBottom = textMarginBottom;
    }

    public enum Type {
        UNKNOWN,
        SONG,
        SCRIPTURE,
        PRESENTATION,
        VIDEO,
        LIVE_VIDEO,
        IMAGE,
        AUDIO,
        WEB
    }

    public enum HorizontalAlignment {
        LEFT,
        CENTER,
        RIGHT,
        DEFAULT
    }

    public enum VerticalAlignment {
        TOP,
        CENTER,
        BOTTOM,
        DEFAULT
    }

    public enum AspectRatio {
        MAINTAIN, // Keep aspect ratio, touch frame from inside
        STRETCH,
        ZOOM // Keep aspect ratio, touch frame from outside
    }

    public enum BackgroundType {
        COLOR,
        GRADIENT,
        IMAGE_TILED,
        IMAGE_SCALED,
        VIDEO,
        LIVE_VIDEO
    }

    public enum GradientStyle {
        HORIZONTAL,
        VERTICAL,
        DIAGONAL_UP,
        DIAGONAL_DOWN
    }

    /**
     * NOTE: Bi-linear is GIMP terminology (though in GIMP the colors are reversed).
     */
    public enum GradientVariant {
        /**
         * Linear gradient: color 1, color 2.
         */
        LINEAR,

        /**
         * Reversed linear gradient: color 2, color 1.
         */
        LINEAR_REVERSED,

        /**
         * Bi-linear gradient: color 1, color 2, color 1.
         */
        BILINEAR,

        /**
         * Reversed bi-linear gradient: color 2, color 1, color 1.
         */
        BILINEAR_REVERSED
    }
}
