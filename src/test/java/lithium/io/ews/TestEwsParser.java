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

import java.awt.*;
import java.io.*;
import java.nio.*;
import java.nio.charset.*;
import java.util.List;

import junit.framework.*;
import lithium.io.rtf.*;

/**
 * Test case for {@link EwsParser}.
 *
 * @author Gerrit Meinders
 */

public class TestEwsParser
        extends TestCase {

    /**
     * Reads a schedule with 3 song entries, which contain some fragments from
     * "Lorem Ipsum".
     */
    public void testSongs()
            throws IOException {
        final byte[] scheduleFile = Tools.loadResource(getClass(), "lorem.ews");

        final EwsParser parser = new EwsParser();
        parser.setCharset(Charset.forName("windows-1252"));
        final Schedule schedule = parser.parse(ByteBuffer.wrap(scheduleFile));

        final List<ScheduleEntry> entries = schedule.getEntries();
        assertEquals("Unexpected number of entries.", 3, entries.size());

        final String[] expectedTitles =
                {
                        "Lorem 1",
                        "Lorem 2",
                        "Lorem 3",
                        };

        final String[] expectedContents =
                {
                        "{\\rtf1\\ansi\\deff0\\deftab254{\\fonttbl{\\f0\\fnil\\fcharset1 Arial;}}{\\colortbl\\red0\\green0\\blue0;\\red255\\green0\\blue0;\\red0\\green128\\blue0;\\red0\\green0\\blue255;\\red255\\green255\\blue0;\\red255\\green0\\blue255;\\red128\\green0\\blue128;\\red128\\green0\\blue0;\\red0\\green255\\blue0;\\red0\\green255\\blue255;\\red0\\green128\\blue128;\\red0\\green0\\blue128;\\red255\\green255\\blue255;\\red192\\green192\\blue192;\\red128\\green128\\blue128;\\red255\\green255\\blue255;}\\paperw12240\\paperh15840\\margl1880\\margr1880\\margt1440\\margb1440{\\*\\pnseclvl1\\pnucrm\\pnstart1\\pnhang\\pnindent720{\\pntxtb}{\\pntxta{.}}}\r\n"
                        + "{\\*\\pnseclvl2\\pnucltr\\pnstart1\\pnhang\\pnindent720{\\pntxtb}{\\pntxta{.}}}\r\n"
                        + "{\\*\\pnseclvl3\\pndec\\pnstart1\\pnhang\\pnindent720{\\pntxtb}{\\pntxta{.}}}\r\n"
                        + "{\\*\\pnseclvl4\\pnlcltr\\pnstart1\\pnhang\\pnindent720{\\pntxtb}{\\pntxta{)}}}\r\n"
                        + "{\\*\\pnseclvl5\\pndec\\pnstart1\\pnhang\\pnindent720{\\pntxtb{(}}{\\pntxta{)}}}\r\n"
                        + "{\\*\\pnseclvl6\\pnlcltr\\pnstart1\\pnhang\\pnindent720{\\pntxtb{(}}{\\pntxta{)}}}\r\n"
                        + "{\\*\\pnseclvl7\\pnlcrm\\pnstart1\\pnhang\\pnindent720{\\pntxtb{(}}{\\pntxta{)}}}\r\n"
                        + "{\\*\\pnseclvl8\\pnlcltr\\pnstart1\\pnhang\\pnindent720{\\pntxtb{(}}{\\pntxta{)}}}\r\n"
                        + "{\\*\\pnseclvl9\\pndec\\pnstart1\\pnhang\\pnindent720{\\pntxtb{(}}{\\pntxta{)}}}\r\n"
                        + "{\\pard\\ql\\li0\\fi0\\ri0\\sb0\\sl\\sa0 \\plain\\f1\\fs18\\fntnamaut Lorem ipsum dolor sit amet, consectetur adipiscing elit. Pellentesque lobortis \\plain\\f1\\fs18\\fntnamaut eros a risus volutpat tempus. Sed id massa non mauris egestas porta bibendum \\plain\\f1\\fs18\\fntnamaut quis eros. Etiam adipiscing ipsum magna, sed iaculis justo tincidunt ut. Vivamus \\plain\\f1\\fs18\\fntnamaut eget velit augue. Phasellus aliquet pretium interdum. Aenean molestie, neque \\plain\\f1\\fs18\\fntnamaut eget placerat cursus, quam sem tristique lorem, ac venenatis tellus velit sit amet \\plain\\f1\\fs18\\fntnamaut neque. Integer et mauris vitae dui lobortis cursus volutpat vitae justo. Nunc vitae \\plain\\f1\\fs18\\fntnamaut lorem non ipsum rutrum sodales nec eget mauris. Quisque molestie tristique \\plain\\f1\\fs18\\fntnamaut laoreet. Pellentesque gravida convallis elit in pretium. Fusce non augue neque. \\plain\\f1\\fs18\\fntnamaut Duis at dolor justo. Nunc ac tellus a nibh tristique euismod nec quis nunc.\\par\r\n"
                        + "\\ql\\li0\\fi0\\ri0\\sb0\\sl\\sa0 \\plain\\f1\\fs18\\fntnamaut }\r\n"
                        + "}",

                        "{\\rtf1\\ansi\\deff0\\deftab254{\\fonttbl{\\f0\\fnil\\fcharset1 Arial;}}{\\colortbl\\red0\\green0\\blue0;\\red255\\green0\\blue0;\\red0\\green128\\blue0;\\red0\\green0\\blue255;\\red255\\green255\\blue0;\\red255\\green0\\blue255;\\red128\\green0\\blue128;\\red128\\green0\\blue0;\\red0\\green255\\blue0;\\red0\\green255\\blue255;\\red0\\green128\\blue128;\\red0\\green0\\blue128;\\red255\\green255\\blue255;\\red192\\green192\\blue192;\\red128\\green128\\blue128;\\red255\\green255\\blue255;}\\paperw12240\\paperh15840\\margl1880\\margr1880\\margt1440\\margb1440{\\*\\pnseclvl1\\pnucrm\\pnstart1\\pnhang\\pnindent720{\\pntxtb}{\\pntxta{.}}}\r\n"
                        + "{\\*\\pnseclvl2\\pnucltr\\pnstart1\\pnhang\\pnindent720{\\pntxtb}{\\pntxta{.}}}\r\n"
                        + "{\\*\\pnseclvl3\\pndec\\pnstart1\\pnhang\\pnindent720{\\pntxtb}{\\pntxta{.}}}\r\n"
                        + "{\\*\\pnseclvl4\\pnlcltr\\pnstart1\\pnhang\\pnindent720{\\pntxtb}{\\pntxta{)}}}\r\n"
                        + "{\\*\\pnseclvl5\\pndec\\pnstart1\\pnhang\\pnindent720{\\pntxtb{(}}{\\pntxta{)}}}\r\n"
                        + "{\\*\\pnseclvl6\\pnlcltr\\pnstart1\\pnhang\\pnindent720{\\pntxtb{(}}{\\pntxta{)}}}\r\n"
                        + "{\\*\\pnseclvl7\\pnlcrm\\pnstart1\\pnhang\\pnindent720{\\pntxtb{(}}{\\pntxta{)}}}\r\n"
                        + "{\\*\\pnseclvl8\\pnlcltr\\pnstart1\\pnhang\\pnindent720{\\pntxtb{(}}{\\pntxta{)}}}\r\n"
                        + "{\\*\\pnseclvl9\\pndec\\pnstart1\\pnhang\\pnindent720{\\pntxtb{(}}{\\pntxta{)}}}\r\n"
                        + "{\\pard\\ql\\li0\\fi0\\ri0\\sb0\\sl\\sa0 \\plain\\f1\\fs18\\fntnamaut Suspendisse consequat ligula et elementum facilisis. Etiam in sollicitudin nisi, \\plain\\f1\\fs18\\fntnamaut ullamcorper faucibus mi. Mauris justo odio, consectetur id tempus ac, interdum \\plain\\f1\\fs18\\fntnamaut ut mauris. Etiam non ligula semper, accumsan tellus vitae, ultricies tellus. Nam \\plain\\f1\\fs18\\fntnamaut dignissim, arcu porta pretium tincidunt, dui arcu dapibus ligula, non ultricies \\plain\\f1\\fs18\\fntnamaut nulla nisi et tellus. Nam non tempus diam, in tempus orci. Nulla congue lobortis \\plain\\f1\\fs18\\fntnamaut interdum.\\par\r\n"
                        + "\\ql\\li0\\fi0\\ri0\\sb0\\sl\\sa0 \\plain\\f1\\fs18\\fntnamaut }\r\n"
                        + "}",

                        "{\\rtf1\\ansi\\deff0\\deftab254{\\fonttbl{\\f0\\fnil\\fcharset1 Arial;}}{\\colortbl\\red0\\green0\\blue0;\\red255\\green0\\blue0;\\red0\\green128\\blue0;\\red0\\green0\\blue255;\\red255\\green255\\blue0;\\red255\\green0\\blue255;\\red128\\green0\\blue128;\\red128\\green0\\blue0;\\red0\\green255\\blue0;\\red0\\green255\\blue255;\\red0\\green128\\blue128;\\red0\\green0\\blue128;\\red255\\green255\\blue255;\\red192\\green192\\blue192;\\red128\\green128\\blue128;\\red255\\green255\\blue255;}\\paperw12240\\paperh15840\\margl1880\\margr1880\\margt1440\\margb1440{\\*\\pnseclvl1\\pnucrm\\pnstart1\\pnhang\\pnindent720{\\pntxtb}{\\pntxta{.}}}\r\n"
                        + "{\\*\\pnseclvl2\\pnucltr\\pnstart1\\pnhang\\pnindent720{\\pntxtb}{\\pntxta{.}}}\r\n"
                        + "{\\*\\pnseclvl3\\pndec\\pnstart1\\pnhang\\pnindent720{\\pntxtb}{\\pntxta{.}}}\r\n"
                        + "{\\*\\pnseclvl4\\pnlcltr\\pnstart1\\pnhang\\pnindent720{\\pntxtb}{\\pntxta{)}}}\r\n"
                        + "{\\*\\pnseclvl5\\pndec\\pnstart1\\pnhang\\pnindent720{\\pntxtb{(}}{\\pntxta{)}}}\r\n"
                        + "{\\*\\pnseclvl6\\pnlcltr\\pnstart1\\pnhang\\pnindent720{\\pntxtb{(}}{\\pntxta{)}}}\r\n"
                        + "{\\*\\pnseclvl7\\pnlcrm\\pnstart1\\pnhang\\pnindent720{\\pntxtb{(}}{\\pntxta{)}}}\r\n"
                        + "{\\*\\pnseclvl8\\pnlcltr\\pnstart1\\pnhang\\pnindent720{\\pntxtb{(}}{\\pntxta{)}}}\r\n"
                        + "{\\*\\pnseclvl9\\pndec\\pnstart1\\pnhang\\pnindent720{\\pntxtb{(}}{\\pntxta{)}}}\r\n"
                        + "{\\pard\\ql\\li0\\fi0\\ri0\\sb0\\sl\\sa0 \\plain\\f1\\fs18\\fntnamaut Suspendisse scelerisque mauris eget interdum hendrerit. Fusce aliquam est id \\plain\\f1\\fs18\\fntnamaut lorem pellentesque, non euismod lacus pellentesque. Nulla facilisi. Nullam \\plain\\f1\\fs18\\fntnamaut ornare pharetra condimentum. Nulla interdum ornare placerat. Phasellus vel \\plain\\f1\\fs18\\fntnamaut lorem sapien. Suspendisse at varius mauris, a ultricies massa. Nullam rhoncus \\plain\\f1\\fs18\\fntnamaut ultricies nunc eget aliquam. Fusce faucibus rhoncus euismod. Cras in hendrerit \\plain\\f1\\fs18\\fntnamaut lectus, id vehicula tellus. Phasellus porta nisi accumsan sapien imperdiet, vel \\plain\\f1\\fs18\\fntnamaut suscipit est porttitor. Nam adipiscing dolor vel ligula vulputate, accumsan \\plain\\f1\\fs18\\fntnamaut blandit erat aliquam. Quisque sodales porta dignissim.\\par\r\n"
                        + "\\ql\\li0\\fi0\\ri0\\sb0\\sl\\sa0 \\plain\\f1\\fs18\\fntnamaut }\r\n"
                        + "}",
                        };

        for (int i = 0; i < 3; i++) {
            final ScheduleEntry entry = entries.get(i);
            assertEquals("Entry " + i + ": unexpected title.", expectedTitles[i], entry.getTitle());

            final Content content = entry.getContent();
            assertTrue("Expected text content, but was: " + content, content instanceof TextContent);

            final TextContent textContent = (TextContent) content;
            assertEquals("Entry " + i + ": unexpected content.", expectedContents[i], RtfWriter.writeToString(textContent.getText()));
        }
    }

    public void testReadScheduleWith2Songs() throws IOException {
        final byte[] scheduleFile = Tools.loadResource(getClass(), "output2.ews");

        final EwsParser parser = new EwsParser();
        parser.setCharset(Charset.forName("windows-1252"));
        final Schedule schedule = parser.parse(ByteBuffer.wrap(scheduleFile));

        assertEquals("Unexpected number of entries.", 2, schedule.getEntries().size());
        assertEquals("Leeg", schedule.getEntries().get(0).getTitle());
        assertEquals("Psalm 001", schedule.getEntries().get(1).getTitle());

        Content content1 = schedule.getEntries().get(0).getContent();
        assertTrue("Expected text content1, but was: " + content1, content1 instanceof TextContent);
        Content content2 = schedule.getEntries().get(1).getContent();
        assertTrue("Expected text content2, but was: " + content2, content2 instanceof TextContent);

        assertEquals("zin 1\n" +
                     "zin 2\n" +
                     "\n" +
                     "verse 2\n" +
                     "zin 3\n" +
                     "zin 4\n" +
                     "zin 5 dit is bold en dit is italic en dit is normaal.\n" +
                     "zin 6\n" +
                     "\n" +
                     "verse 3\n" +
                     "zin 7\n" +
                     "shift enter zin 8\n" +
                     "zin 9\n" +
                     "zin 10 heeft zwarte kleuren\n\n",
                     TestUtils.getTextFromContent((TextContent) content1).replace("\r", ""));

        assertEquals("Verse 1\n" +
                     "Gezegend hij, die in der bozen raad\n" +
                     "niet wandelt, noch met goddelozen gaat,\n" +
                     "noch zich door spotters in de kring laat noden,\n" +
                     "\n" +
                     "waar ieder lacht met God en zijn geboden,\n" +
                     "maar die aan 's Heren wet zijn vreugde heeft\n" +
                     "en dag en nacht met zijn geboden leeft.\n" +
                     "\n" +
                     "Verse 2\n" +
                     "Hij is een groene boom die staat geplant\n" +
                     "waar waterbeken vloeien door het land.\n" +
                     "Zijn loof behoeft de droogte niet te duchten,\n" +
                     "\n" +
                     "te goeder tijd geeft hij zijn rijpe vruchten.\n" +
                     "Gezegend die zich aan Gods wetten voedt:\n" +
                     "het gaat hem wel in alles wat hij doet.\n" +
                     "\n" +
                     "Verse 3\n" +
                     "Gans anders zal 't de goddelozen gaan:\n" +
                     "zij zijn het kaf dat wegwaait van het graan.\n" +
                     "Zij kunnen zich voor God niet staande houden,\n" +
                     "\n" +
                     "er is geen plaats voor hen bij zijn vertrouwden.\n" +
                     "God kent die wandelt in het rechte spoor,\n" +
                     "wie Hem verlaat gaat dwalende teloor.\n" +
                     "\n",
                     TestUtils.getTextFromContent((TextContent) content2).replace("\r", ""));
    }

    public void testReadFileWithSpecialChars() throws IOException {
        final byte[] scheduleFile = Tools.loadResource(getClass(), "special_chars.ews");

        EwsParser parser = new EwsParser();
        Schedule schedule = parser.parse(ByteBuffer.wrap(scheduleFile));

        assertEquals("Unexpected number of entries.", 1, schedule.getEntries().size());
        assertEquals("Leeg", schedule.getEntries().get(0).getTitle());

        Content content1 = schedule.getEntries().get(0).getContent();
        assertTrue("Expected text content1, but was: " + content1, content1 instanceof TextContent);

        assertEquals("Special chars: a,b,c,d,e, (', ^, \", `) but for real: \n" +
                     "e: é, ê, ë, è\n" +
                     "a: á, â, ä, à\n" +
                     "u: ú, û, ü, ù\n" +
                     "i: í, î, ï, ì\n" +
                     "o: ó, ô, ö, ò\n" +
                     "E: É, Ê, Ë, È\n" +
                     "A: Á, Â, Ä, À\n" +
                     "U: Ú, Û, Ü, Ù\n" +
                     "I: Í, Î, Ï, Ì\n" +
                     "O: Ó, Ô, Ö, Ò\n" +
                     "\n" +
                     "`1234567890-=\n" +
                     "~!@#$%^&*()_+\n" +
                     "[]\\ - {}|\n" +
                     ";' - :\"\n" +
                     ",./ - <>?\n" +
                     "¡²³¤€¼½¾‘’¥×\n" +
                     "äåé®þüúíóö«»¬\n" +
                     "áßðø¶´\n" +
                     "æ©ñµç¿\n" +
                     "ÄÅÉ®ÞÜÚÍÓÖ«»¬\n" +
                     "ÁßÐØ¶´\n" +
                     "Æ©ÑµÇ¿",
                     TestUtils.getTextFromContent((TextContent) content1).replace("\r", "").trim());
    }

    public void testReadFileWithBackgroundColor() throws IOException {
        final byte[] scheduleFile = Tools.loadResource(getClass(), "schedule_with_background_color_red.ews");

        EwsParser parser = new EwsParser();
        Schedule schedule = parser.parse(ByteBuffer.wrap(scheduleFile));

        assertEquals(1, schedule.getEntries().size());

        assertTrue("Background isn't the expected ColorBackground",
                   schedule.getEntries().get(0).getBackground() instanceof ColorBackground);
        ColorBackground background = (ColorBackground) schedule.getEntries().get(0).getBackground();
        assertEquals(Color.RED, background.getColor());
    }

    public void testReadFileWithBackgroundImage() throws IOException {
        final byte[] scheduleFile = Tools.loadResource(getClass(), "schedule_with_background_image.ews");

        EwsParser parser = new EwsParser();
        Schedule schedule = parser.parse(ByteBuffer.wrap(scheduleFile));

        assertEquals(1, schedule.getEntries().size());

        assertTrue("Background isn't the expected ImageBackground",
                   schedule.getEntries().get(0).getBackground() instanceof ImageBackground);
        ImageBackground background = (ImageBackground) schedule.getEntries().get(0).getBackground();
        byte[] backgroundBytes = background.getImage().getBytes();
        assertEquals("C:\\Users\\samuel\\Downloads\\image1.jpg", background.getName());
        assertEquals(5884, backgroundBytes.length);
    }

    public void testReadFileWithPresentation() throws IOException {
        // Given
        final byte[] scheduleFile = Tools.loadResource( getClass(), "presentations/schedule_with_presentation_between_two_songs win 8.ews" );
        final byte[] powerpointFile = Tools.loadResource( getClass(), "presentations/powerpoint_with_2_slides.pptx" );
        final byte[] powerpointFileSlideImage1 = Tools.loadResource( getClass(), "presentations/powerpoint_with_2_slides_slide_1.jpg" );
        final byte[] powerpointFileSlideImage2 = Tools.loadResource( getClass(), "presentations/powerpoint_with_2_slides_slide_2.jpg" );

        // When
        EwsParser parser = new EwsParser();
        Schedule schedule = parser.parse( ByteBuffer.wrap( scheduleFile ) );

        // Then
        assertEquals( 3, schedule.getEntries().size() );
        assertEquals( "10,000 Reasons (Bless the Lord)", schedule.getEntries().get( 0 ).getTitle() );
        assertEquals( "powerpoint_with_2_slides.pptx", schedule.getEntries().get( 1 ).getTitle() );
        assertEquals( "Be Thou my vision", schedule.getEntries().get( 2 ).getTitle() );

        assertTrue( "Missing presentation content in schedule entry", schedule.getEntries().get( 1 ).getContent() instanceof BinaryContent );
        assertEquals( powerpointFile.length, ( (BinaryContent)schedule.getEntries().get( 1 ).getContent() ).getBytes().length );

        Presentation presentation = schedule.getEntries().get( 1 ).getPresentation();
        assertNotNull( "Missing presentation attribute of schedule entry", presentation );
        assertEquals( 35864, presentation.getMagicValue() );

        assertEquals( 2, presentation.getSlides().size() );
        assertEquals( powerpointFileSlideImage1.length, presentation.getSlides().get( 0 ).getContent().length );
        assertEquals( powerpointFileSlideImage2.length, presentation.getSlides().get( 1 ).getContent().length );
    }
}
