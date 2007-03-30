package descent.ui.metrics.export.html;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

final class HtmlPageBuilder {
    private Date attributionDate;
    private PrintWriter writer;
    private File directory;

    public HtmlPageBuilder(File directory) {
        this.directory = directory;
        attributionDate = new Date();
    }

    public HtmlPageBuilder openPage(String fileName, String heading) throws IOException {
        initialiseWriter(fileName);
        openHtmlPage();
        writePageHeadElement(heading);
        openPageBody(heading);

        return this;
    }

    private void openPageBody(String heading) {
        openElementAndNewline("BODY");
        createElementAndNewline("H1", heading);

        printAttribution();
    }

    private void openHtmlPage() {
        println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\"/>");
        openElementAndNewline("HTML");
    }

    private void writePageHeadElement(String heading) {
        openElementAndNewline("HEAD");
        createElementAndNewline("TITLE", heading);
        println("<link href=\"style.css\" type=\"text/css\" rel=\"STYLESHEET\"/>");
        closeElementAndNewline("HEAD");
    }

    public void closePage() {
        printAttribution();

        closeElementAndNewline("BODY");
        closeElementAndNewline("HTML");
        writer.close();
    }

    private void printAttribution() {
        openElement("P").openElement("FONT size=\"-2\"");
        print("Produced by ").print(getLink("http://www.stateofflow.com", "State Of Flow"));
        print(' ').print(getLink("http://www.stateofflow.com/projects/16/eclipsemetrics", "Eclipse Metrics"));
        print(" on ").println(attributionDate.toString());
        closeElement("FONT");
    }
    
    public void createImageFileLink(String imageFileName) {
        if (imageFileName != null) {
            print("<IMG src=\"").print(imageFileName).println("\"/>").openElement("P");
        }
    }
    
    public HtmlPageBuilder createElementAndNewline(String name, String cdata) {
        createElement(name, cdata);
        println();
        return this;
    }

    public HtmlPageBuilder createElement(String name, String cdata) {
        openElement(name);
        print(cdata);
        closeElement(name);
        return this;
    }

    public HtmlPageBuilder openElementAndNewline(String name) {
        openElement(name);
        println();
        return this;
    }

    public HtmlPageBuilder openElement(String name) {
        print('<');
        print(name);
        print('>');
        return this;
    }

    public HtmlPageBuilder closeElementAndNewline(String name) {
        closeElement(name);
        println();
        return this;
    }

    public HtmlPageBuilder closeElement(String name) {
        print("</");
        print(name);
        print('>');
        return this;
    }

    public HtmlPageBuilder printLinkAndNewline(String to, String cdata) {
        printLink(to, cdata);
        println();
        return this;
    }

    public HtmlPageBuilder printLink(String to, String cdata) {
        print(getLink(to + ".html", cdata));
        return this;
    }

    public String getHtmlLink(String to, String cdata) {
        return getLink(to + ".html", cdata);
    }

    public String getLink(String url, String cdata) {
        StringBuffer sb = new StringBuffer();
        sb.append("<A HREF=").append('"').append(url).append("\">");
        sb.append(cdata);
        sb.append("</A>");

        return sb.toString();
    }

    public HtmlPageBuilder print(String s) {
        writer.print(s);
        return this;
    }

    public HtmlPageBuilder println(String s) {
        writer.println(s);
        return this;
    }

    public HtmlPageBuilder print(char c) {
        writer.print(c);
        return this;
    }

    public HtmlPageBuilder println() {
        writer.println();
        return this;
    }

    private void initialiseWriter(String pageName) throws IOException {
        writer = new PrintWriter(new BufferedWriter(new FileWriter(new File(directory, pageName + ".html"))));
    }
}
