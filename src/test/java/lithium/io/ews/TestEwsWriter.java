/*
 * Copyright 2013-2021 Gerrit Meinders
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

import lithium.io.Config;
import lithium.io.rtf.RtfGroup;
import lithium.io.rtf.TextNode;
import org.junit.Test;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Test case for {@link EwsParser}.
 *
 * @author Gerrit Meinders
 */

public class TestEwsWriter
{
    @Test
    public void testWriteScheduleFile() throws IOException {
        TextNode rtfNode1 = new TextNode();
        rtfNode1.setText("zin 1\n" +
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
                         "zin 10 heeft zwarte kleuren\n");

        RtfGroup rtfGroup1 = new RtfGroup();
        rtfGroup1.addNode(rtfNode1);

        TextContent content1 = new TextContent();
        content1.setText(rtfGroup1);

        ScheduleEntry scheduleEntry1 = new ScheduleEntry();
        scheduleEntry1.setTitle("Leeg");
        scheduleEntry1.setContent(content1);

        TextNode rtfNode2 = new TextNode();
        rtfNode2.setText("Verse 1\n" +
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
                         "\n" +
                         "\n");

        RtfGroup rtfGroup2 = new RtfGroup();
        rtfGroup2.addNode(rtfNode2);

        TextContent content2 = new TextContent();
        content2.setText(rtfGroup2);

        ScheduleEntry scheduleEntry2 = new ScheduleEntry();
        scheduleEntry2.setTitle("Psalm 001");
        scheduleEntry2.setContent(content2);

        Schedule schedule = new Schedule();
        schedule.getEntries().add(scheduleEntry1);
        schedule.getEntries().add(scheduleEntry2);

        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final EwsWriter writer = new EwsWriter( out );
        writer.setCharset(Charset.forName("windows-1252"));
        writer.write(schedule);

        final byte[] scheduleFileActual = out.toByteArray();
        final byte[] scheduleFileExpected = Tools.loadResource(getClass(), "output1.ews");

        assertEquals(scheduleFileExpected.length, scheduleFileActual.length);
        for (int i = 0; i < scheduleFileExpected.length; i++) {
            assertEquals("Byte at index " + i + " are not equal: files are not equal", scheduleFileExpected[i], scheduleFileActual[i]);
        }
    }

    @Test
    public void testWriteScheduleFileWithBoilerplate() throws IOException {
        Schedule schedule = new Schedule();
        schedule.getEntries().add(TestUtils.createEntry("Leeg", "zin 1\n" +
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
                                                                "zin 10 heeft zwarte kleuren\n"));

        schedule.getEntries().add(TestUtils.createEntry("Psalm 001", "Verse 1\n" +
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
                                                                     "wie Hem verlaat gaat dwalende teloor.\n"));

        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final EwsWriter writer = new EwsWriter( out );
        writer.setCharset(Charset.forName("windows-1252"));
        writer.write(schedule);

        final byte[] scheduleFileActual = out.toByteArray();
        final byte[] scheduleFileExpected = Tools.loadResource(getClass(), "output3.ews");

        assertEquals(scheduleFileExpected.length, scheduleFileActual.length);
        for (int i = 0; i < scheduleFileExpected.length; i++) {
            assertEquals("Byte at index " + i + " are not equal: files are not equal", scheduleFileExpected[i], scheduleFileActual[i]);
        }
    }

    @Test
    public void testWriteAndThenReadScheduleFile() throws IOException {
        // Write
        Schedule writeSchedule = new Schedule();
        writeSchedule.getEntries().add(TestUtils.createEntry("Leeg", "first sentence 1\n" +
                                                                     "What {about} a seemingly control symbol: \\\n" +
                                                                     "next sentence has a special char: é\n"));

        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final EwsWriter writer = new EwsWriter( out );
        writer.setCharset(Charset.forName(Config.charset));
        writer.write(writeSchedule);

        // Read
        final byte[] scheduleFileActual = out.toByteArray();

        EwsParser parser = new EwsParser();
        parser.setCharset(Charset.forName(Config.charset));
        Schedule readSchedule = parser.parse(ByteBuffer.wrap(scheduleFileActual));

        assertEquals("Unexpected number of entries.", 1, readSchedule.getEntries().size());
        assertEquals("Leeg", readSchedule.getEntries().get(0).getTitle());

        Content content1 = readSchedule.getEntries().get(0).getContent();
        assertTrue("Expected text content1, but was: " + content1, content1 instanceof TextContent);

        assertEquals("first sentence 1\n" +
                     "What {about} a seemingly control symbol: \\\n" +
                     "next sentence has a special char: é",
                     TestUtils.getTextFromContent((TextContent) content1).replace("\r", "").trim());
    }

    @Test
    public void testWriteScheduleWithBackgroundColor() throws IOException {
        // Write
        Color color = new Color(100, 200, 255);
        ScheduleEntry entry1 = TestUtils.createEntry("Song with color background", "first sentence 1");
        entry1.setBackground(new ColorBackground(color));

        Schedule writeSchedule = new Schedule();
        writeSchedule.getEntries().add(entry1);

        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final EwsWriter writer = new EwsWriter( out );
        writer.write(writeSchedule);

        // Read
        final byte[] scheduleFileActual = out.toByteArray();

        EwsParser parser = new EwsParser();
        Schedule readSchedule = parser.parse(ByteBuffer.wrap(scheduleFileActual));

        ColorBackground background = (ColorBackground) readSchedule.getEntries().get(0).getBackground();
        assertEquals(color, background.getColor());
    }

    @Test
    public void testWriteScheduleWithBackgroundImage() throws IOException {
        // Write
        final byte[] image = Tools.loadResource(getClass(), "image1.jpg");

        ScheduleEntry entry1 = TestUtils.createEntry("Song with image background", "first sentence 1");
        entry1.setBackground(new ImageBackground("image1.jpg", image));

        Schedule writeSchedule = new Schedule();
        writeSchedule.getEntries().add(entry1);

        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final EwsWriter writer = new EwsWriter( out );
        writer.write(writeSchedule);

        // Read
        final byte[] scheduleFileActual = out.toByteArray();

        EwsParser parser = new EwsParser();
        Schedule readSchedule = parser.parse(ByteBuffer.wrap(scheduleFileActual));

        ImageBackground background = (ImageBackground) readSchedule.getEntries().get(0).getBackground();
        assertEquals("image1.jpg", background.getName());
        assertEquals(image.length, background.getImage().getBytes().length);
        for (int i = 0; i < image.length; i++) {
            assertEquals("Byte at index " + i + " are not equal: images are not equal", image[i], background.getImage().getBytes()[i]);
        }
    }

    @Test
    public void testWriteScheduleWithBackgroundVideo() throws IOException {
        // Write
        final byte[] image = Tools.loadResource(getClass(), "image1.jpg");
        final byte[] video = {0, 1, 2};

        ScheduleEntry entry1 = TestUtils.createEntry("Song with video background", "first sentence 1");
        entry1.setBackground(new VideoBackground("video.mp4", image, video));

        Schedule writeSchedule = new Schedule();
        writeSchedule.getEntries().add(entry1);

        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final EwsWriter writer = new EwsWriter( out );
        writer.write(writeSchedule);

        // Read
        final byte[] scheduleFileActual = out.toByteArray();

        EwsParser parser = new EwsParser();
        Schedule readSchedule = parser.parse(ByteBuffer.wrap(scheduleFileActual));

        VideoBackground background = (VideoBackground) readSchedule.getEntries().get(0).getBackground();
        assertEquals("video.mp4", background.getName());
        assertEquals(image.length, background.getImage().getBytes().length);
        for (int i = 0; i < image.length; i++) {
            assertEquals("Byte at index " + i + " are not equal: images are not equal", image[i], background.getImage().getBytes()[i]);
        }

        assertEquals(video.length, background.getVideo().getBytes().length);
        for (int i = 0; i < video.length; i++) {
            assertEquals("Byte at index " + i + " are not equal: videos are not equal", video[i], background.getVideo().getBytes()[i]);
        }
    }
}
