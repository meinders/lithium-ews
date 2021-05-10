package lithium.io.ews;

import lithium.io.rtf.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TestUtils {
    public static ArrayList<RtfNode> createContentLine(String line) {
        return new ArrayList<RtfNode>(
                Arrays.asList(
                        new ControlWord("ql", null, false),
                        new ControlWord("li", 0, false),
                        new ControlWord("fi", 0, false),
                        new ControlWord("ri", 0, false),
                        new ControlWord("sb", 0, false),
                        new ControlWord("sl", null, false),
                        new ControlWord("sa", 0, true),
                        new ControlWord("plain", null, false),
                        new ControlWord("f", 0, false),
                        new ControlWord("fs", 20, false),
                        new ControlWord("shad", 0, false),
                        new ControlWord("outl", 0, false),
                        new ControlWord("b", 0, false),
                        new ControlWord("i", 0, true),
                        new TextNode(line),
                        new ControlWord("par", null, false),
                        new TextNode("\r\n")));
    }

    public static List<RtfNode> createContent(String content) {
        return Arrays.stream(content.split("\n"))
                .flatMap(it -> createContentLine(it).stream())
                .collect(Collectors.toList());
    }

    public static RtfGroup createRtfGroup(String content) {
        return new RtfGroup(new ArrayList<RtfNode>(
                Arrays.asList(
                        new ControlWord("rtf", 1, false),
                        new ControlWord("ansi", null, false),
                        new ControlWord("deff", 0, false),
                        new ControlWord("deftab", 254, false),
                        new RtfGroup(new ArrayList<RtfNode>(
                                Arrays.asList(
                                        new ControlWord("fonttbl", null, false),
                                        new RtfGroup(new ArrayList<RtfNode>(
                                                Arrays.asList(
                                                        new ControlWord("f", 0, false),
                                                        new ControlWord("fnil", null, false),
                                                        new ControlWord("fcharset", 1, true),
                                                        new TextNode("Arial;"))))
                                             ))),
                        new RtfGroup(new ArrayList<RtfNode>(
                                Arrays.asList(
                                        new ControlWord("colortbl", null, false),
                                        new ControlWord("red", 0, false),
                                        new ControlWord("green", 0, false),
                                        new ControlWord("blue", 0, false),
                                        new TextNode(";"),
                                        new ControlWord("red", 255, false),
                                        new ControlWord("green", 0, false),
                                        new ControlWord("blue", 0, false),
                                        new TextNode(";"),
                                        new ControlWord("red", 0, false),
                                        new ControlWord("green", 128, false),
                                        new ControlWord("blue", 0, false),
                                        new TextNode(";"),
                                        new ControlWord("red", 0, false),
                                        new ControlWord("green", 0, false),
                                        new ControlWord("blue", 255, false),
                                        new TextNode(";"),
                                        new ControlWord("red", 255, false),
                                        new ControlWord("green", 255, false),
                                        new ControlWord("blue", 0, false),
                                        new TextNode(";"),
                                        new ControlWord("red", 255, false),
                                        new ControlWord("green", 0, false),
                                        new ControlWord("blue", 255, false),
                                        new TextNode(";"),
                                        new ControlWord("red", 128, false),
                                        new ControlWord("green", 0, false),
                                        new ControlWord("blue", 128, false),
                                        new TextNode(";"),
                                        new ControlWord("red", 128, false),
                                        new ControlWord("green", 0, false),
                                        new ControlWord("blue", 0, false),
                                        new TextNode(";"),
                                        new ControlWord("red", 0, false),
                                        new ControlWord("green", 255, false),
                                        new ControlWord("blue", 0, false),
                                        new TextNode(";"),
                                        new ControlWord("red", 0, false),
                                        new ControlWord("green", 255, false),
                                        new ControlWord("blue", 255, false),
                                        new TextNode(";"),
                                        new ControlWord("red", 0, false),
                                        new ControlWord("green", 128, false),
                                        new ControlWord("blue", 128, false),
                                        new TextNode(";"),
                                        new ControlWord("red", 0, false),
                                        new ControlWord("green", 0, false),
                                        new ControlWord("blue", 128, false),
                                        new TextNode(";"),
                                        new ControlWord("red", 255, false),
                                        new ControlWord("green", 255, false),
                                        new ControlWord("blue", 255, false),
                                        new TextNode(";"),
                                        new ControlWord("red", 192, false),
                                        new ControlWord("green", 192, false),
                                        new ControlWord("blue", 192, false),
                                        new TextNode(";"),
                                        new ControlWord("red", 128, false),
                                        new ControlWord("green", 128, false),
                                        new ControlWord("blue", 128, false),
                                        new TextNode(";"),
                                        new ControlWord("red", 255, false),
                                        new ControlWord("green", 255, false),
                                        new ControlWord("blue", 255, false),
                                        new TextNode(";")))),
                        new ControlWord("paperw", 12240, false),
                        new ControlWord("paperh", 15840, false),
                        new ControlWord("margl", 1880, false),
                        new ControlWord("margr", 1880, false),
                        new ControlWord("margt", 1440, false),
                        new ControlWord("margb", 1440, false),
                        new RtfGroup(new ArrayList<RtfNode>(
                                Arrays.asList(
                                        new ControlSymbol('*'),
                                        new ControlWord("pnseclvl", 1, false),
                                        new ControlWord("pnucrm", null, false),
                                        new ControlWord("pnstart", 1, false),
                                        new ControlWord("pnhang", null, false),
                                        new ControlWord("pnindent", 720, false),
                                        new RtfGroup(new ArrayList<RtfNode>(
                                                Arrays.asList(new ControlWord("pntxtb", null, false)))),
                                        new RtfGroup(new ArrayList<RtfNode>(
                                                Arrays.asList(
                                                        new ControlWord("pntxta", null, false),
                                                        new RtfGroup(new ArrayList<RtfNode>(
                                                                Arrays.asList(new TextNode("."))))
                                                             )))))),
                        new TextNode("\r\n"),
                        new RtfGroup(new ArrayList<RtfNode>(
                                Arrays.asList(
                                        new ControlSymbol('*'),
                                        new ControlWord("pnseclvl", 2, false),
                                        new ControlWord("pnucltr", null, false),
                                        new ControlWord("pnstart", 1, false),
                                        new ControlWord("pnhang", null, false),
                                        new ControlWord("pnindent", 720, false),
                                        new RtfGroup(new ArrayList<RtfNode>(
                                                Arrays.asList(
                                                        new ControlWord("pntxtb", null, false)))),
                                        new RtfGroup(new ArrayList<RtfNode>(
                                                Arrays.asList(
                                                        new ControlWord("pntxta", null, false),
                                                        new RtfGroup(new ArrayList<RtfNode>(
                                                                Arrays.asList(new TextNode("."))))
                                                             )))
                                             ))),
                        new TextNode("\r\n"),
                        new RtfGroup(new ArrayList<RtfNode>(
                                Arrays.asList(
                                        new ControlSymbol('*'),
                                        new ControlWord("pnseclvl", 3, false),
                                        new ControlWord("pndec", null, false),
                                        new ControlWord("pnstart", 1, false),
                                        new ControlWord("pnhang", null, false),
                                        new ControlWord("pnindent", 720, false),
                                        new RtfGroup(new ArrayList<RtfNode>(
                                                Arrays.asList(
                                                        new ControlWord("pntxtb", null, false)))),
                                        new RtfGroup(new ArrayList<RtfNode>(
                                                Arrays.asList(
                                                        new ControlWord("pntxta", null, false),
                                                        new RtfGroup(new ArrayList<RtfNode>(
                                                                Arrays.asList(
                                                                        new TextNode(".")))))))))),
                        new TextNode("\r\n"),
                        new RtfGroup(new ArrayList<RtfNode>(
                                Arrays.asList(
                                        new ControlSymbol('*'),
                                        new ControlWord("pnseclvl", 4, false),
                                        new ControlWord("pnlcltr", null, false),
                                        new ControlWord("pnstart", 1, false),
                                        new ControlWord("pnhang", null, false),
                                        new ControlWord("pnindent", 720, false),
                                        new RtfGroup(new ArrayList<RtfNode>(
                                                Arrays.asList(
                                                        new ControlWord("pntxtb", null, false)))),
                                        new RtfGroup(new ArrayList<RtfNode>(
                                                Arrays.asList(
                                                        new ControlWord("pntxta", null, false),
                                                        new RtfGroup(new ArrayList<RtfNode>(
                                                                Arrays.asList(
                                                                        new TextNode(")")))))))))),
                        new TextNode("\r\n"),
                        new RtfGroup(new ArrayList<RtfNode>(
                                Arrays.asList(
                                        new ControlSymbol('*'),
                                        new ControlWord("pnseclvl", 5, false),
                                        new ControlWord("pndec", null, false),
                                        new ControlWord("pnstart", 1, false),
                                        new ControlWord("pnhang", null, false),
                                        new ControlWord("pnindent", 720, false),
                                        new RtfGroup(new ArrayList<RtfNode>(
                                                Arrays.asList(
                                                        new ControlWord("pntxtb", null, false),
                                                        new RtfGroup(new ArrayList<RtfNode>(
                                                                Arrays.asList(
                                                                        new TextNode("("))))))),
                                        new RtfGroup(new ArrayList<RtfNode>(
                                                Arrays.asList(
                                                        new ControlWord("pntxta", null, false),
                                                        new RtfGroup(new ArrayList<RtfNode>(
                                                                Arrays.asList(
                                                                        new TextNode(")")))))))))),
                        new TextNode("\r\n"),
                        new RtfGroup(new ArrayList<RtfNode>(
                                Arrays.asList(
                                        new ControlSymbol('*'),
                                        new ControlWord("pnseclvl", 6, false),
                                        new ControlWord("pnlcltr", null, false),
                                        new ControlWord("pnstart", 1, false),
                                        new ControlWord("pnhang", null, false),
                                        new ControlWord("pnindent", 720, false),
                                        new RtfGroup(new ArrayList<RtfNode>(
                                                Arrays.asList(
                                                        new ControlWord("pntxtb", null, false),
                                                        new RtfGroup(new ArrayList<RtfNode>(
                                                                Arrays.asList(
                                                                        new TextNode("("))))))),
                                        new RtfGroup(new ArrayList<RtfNode>(
                                                Arrays.asList(
                                                        new ControlWord("pntxta", null, false),
                                                        new RtfGroup(new ArrayList<RtfNode>(
                                                                Arrays.asList(
                                                                        new TextNode(")")))))))))),
                        new TextNode("\r\n"),
                        new RtfGroup(new ArrayList<RtfNode>(
                                Arrays.asList(
                                        new ControlSymbol('*'),
                                        new ControlWord("pnseclvl", 7, false),
                                        new ControlWord("pnlcrm", null, false),
                                        new ControlWord("pnstart", 1, false),
                                        new ControlWord("pnhang", null, false),
                                        new ControlWord("pnindent", 720, false),
                                        new RtfGroup(new ArrayList<RtfNode>(
                                                Arrays.asList(
                                                        new ControlWord("pntxtb", null, false),
                                                        new RtfGroup(new ArrayList<RtfNode>(
                                                                Arrays.asList(
                                                                        new TextNode("("))))))),
                                        new RtfGroup(new ArrayList<RtfNode>(
                                                Arrays.asList(
                                                        new ControlWord("pntxta", null, false),
                                                        new RtfGroup(new ArrayList<RtfNode>(
                                                                Arrays.asList(
                                                                        new TextNode(")")))))))))),
                        new TextNode("\r\n"),
                        new RtfGroup(new ArrayList<RtfNode>(
                                Arrays.asList(
                                        new ControlSymbol('*'),
                                        new ControlWord("pnseclvl", 8, false),
                                        new ControlWord("pnlcltr", null, false),
                                        new ControlWord("pnstart", 1, false),
                                        new ControlWord("pnhang", null, false),
                                        new ControlWord("pnindent", 720, false),
                                        new RtfGroup(new ArrayList<RtfNode>(
                                                Arrays.asList(
                                                        new ControlWord("pntxtb", null, false),
                                                        new RtfGroup(new ArrayList<RtfNode>(
                                                                Arrays.asList(
                                                                        new TextNode("("))))))),
                                        new RtfGroup(new ArrayList<RtfNode>(
                                                Arrays.asList(
                                                        new ControlWord("pntxta", null, false),
                                                        new RtfGroup(new ArrayList<RtfNode>(
                                                                Arrays.asList(
                                                                        new TextNode(")")))))))))),
                        new TextNode("\r\n"),
                        new RtfGroup(new ArrayList<RtfNode>(
                                Arrays.asList(
                                        new ControlSymbol('*'),
                                        new ControlWord("pnseclvl", 9, false),
                                        new ControlWord("pndec", null, false),
                                        new ControlWord("pnstart", 1, false),
                                        new ControlWord("pnhang", null, false),
                                        new ControlWord("pnindent", 720, false),
                                        new RtfGroup(new ArrayList<RtfNode>(
                                                Arrays.asList(
                                                        new ControlWord("pntxtb", null, false),
                                                        new RtfGroup(new ArrayList<RtfNode>(
                                                                Arrays.asList(
                                                                        new TextNode("("))))))),
                                        new RtfGroup(new ArrayList<RtfNode>(
                                                Arrays.asList(
                                                        new ControlWord("pntxta", null, false),
                                                        new RtfGroup(new ArrayList<RtfNode>(
                                                                Arrays.asList(
                                                                        new TextNode(")")))))))))),
                        new TextNode("\r\n"),
                        new RtfGroup(
                                Stream.concat(
                                        Stream.of(new ControlWord("pard", null, false)),
                                        createContent(content).stream()
                                             ).collect(Collectors.toList())
                        ),
                        new TextNode("\r\n"))));
    }

    public static ScheduleEntry createEntry(String title, String content) {
        RtfGroup rtfGroup = createRtfGroup(content);

        TextContent rtfContent = new TextContent(rtfGroup);

        return new ScheduleEntry(title, rtfContent);
    }

    public static String getTextFromContent(TextContent content) {
        StringBuilder output = new StringBuilder();
        boolean endOfHeaderStarted = false;
        boolean headerEnded = false;

        for (RtfNode node : content.getText().getNodes()) {
            if (!headerEnded) {
                if (!(node instanceof RtfGroup)) {
                    continue;
                }

                String nodeString = RtfWriter.writeToString((RtfGroup) node);
                if (nodeString.startsWith("{\\*\\") && nodeString.endsWith("{\\pntxtb{(}}{\\pntxta{)}}}")) {
                    endOfHeaderStarted = true;
                } else if (endOfHeaderStarted) {
                    headerEnded = true;
                }
            }

            if (headerEnded) {
                getTextFromNode(output, node);
            }
        }

        return output.toString();
    }

    public static void getTextFromGroup(StringBuilder output, RtfGroup group) {
        for (RtfNode node : group.getNodes()) {
            getTextFromNode(output, node);
        }
    }

    public static void getTextFromNode(StringBuilder output, RtfNode node) {
        if (node instanceof RtfGroup) {
            getTextFromGroup(output, (RtfGroup) node);
        } else if (node instanceof TextNode) {
            String text = insertSpecialChars(((TextNode) node).getText());
            output.append(text);
        } else if (node instanceof ControlWord) {
            if ("line" .equals(((ControlWord) node).getWord())) {
                output.append("\n");
            }
        } else if (node instanceof ControlSymbol) {
            if ('\\' == ((ControlSymbol) node).getSymbol()) {
                output.append("\\");
            } else if ('{' == ((ControlSymbol) node).getSymbol()) {
                output.append("{");
            } else if ('}' == ((ControlSymbol) node).getSymbol()) {
                output.append("}");
            }
        }
    }

    public static String insertSpecialChars(String text) {
        return text
                .replaceAll("^e9", "é")
                .replaceAll("^ea", "ê")
                .replaceAll("^eb", "ë")
                .replaceAll("^e8", "è")
                .replaceAll("^e1", "á")
                .replaceAll("^e2", "â")
                .replaceAll("^e4", "ä")
                .replaceAll("^e0", "à")
                .replaceAll("^e5", "å")
                .replaceAll("^fa", "ú")
                .replaceAll("^fb", "û")
                .replaceAll("^fc", "ü")
                .replaceAll("^f9", "ù")
                .replaceAll("^ed", "í")
                .replaceAll("^ee", "î")
                .replaceAll("^ef", "ï")
                .replaceAll("^ec", "ì")
                .replaceAll("^f3", "ó")
                .replaceAll("^f4", "ô")
                .replaceAll("^f6", "ö")
                .replaceAll("^f2", "ò")
                .replaceAll("^f0", "ð")
                .replaceAll("^f8", "ø")
                .replaceAll("^c9", "É")
                .replaceAll("^ca", "Ê")
                .replaceAll("^cb", "Ë")
                .replaceAll("^c8", "È")
                .replaceAll("^c1", "Á")
                .replaceAll("^c2", "Â")
                .replaceAll("^c4", "Ä")
                .replaceAll("^c0", "À")
                .replaceAll("^c5", "Å")
                .replaceAll("^da", "Ú")
                .replaceAll("^db", "Û")
                .replaceAll("^dc", "Ü")
                .replaceAll("^d9", "Ù")
                .replaceAll("^cd", "Í")
                .replaceAll("^ce", "Î")
                .replaceAll("^cf", "Ï")
                .replaceAll("^cc", "Ì")
                .replaceAll("^d3", "Ó")
                .replaceAll("^d4", "Ô")
                .replaceAll("^d6", "Ö")
                .replaceAll("^d2", "Ò")
                .replaceAll("^a1", "¡")
                .replaceAll("^b2", "²")
                .replaceAll("^b3", "³")
                .replaceAll("^a4", "¤")
                .replaceAll("^80", "€")
                .replaceAll("^bc", "¼")
                .replaceAll("^bd", "½")
                .replaceAll("^be", "¾")
                .replaceAll("^91", "‘")
                .replaceAll("^92", "’")
                .replaceAll("^a5", "¥")
                .replaceAll("^d7", "×")
                .replaceAll("^ae", "®")
                .replaceAll("^df", "ß")
                .replaceAll("^b4", "´")
                .replaceAll("^e6", "æ")
                .replaceAll("^a9", "©")
                .replaceAll("^f1", "ñ")
                .replaceAll("^b5", "µ")
                .replaceAll("^e7", "ç")
                .replaceAll("^bf", "¿")
                .replaceAll("^ae", "®")
                .replaceAll("^c6", "Æ")
                .replaceAll("^d1", "Ñ")
                .replaceAll("^b5", "µ")
                .replaceAll("^b6", "¶")
                .replaceAll("^de", "Þ")
                .replaceAll("^ab", "«")
                .replaceAll("^bb", "»")
                .replaceAll("^d0", "Ð")
                .replaceAll("^d8", "Ø")
                .replaceAll("^ac", "¬")
                .replaceAll("^fe", "þ")
                .replaceAll("^c7", "Ç");
    }
}
