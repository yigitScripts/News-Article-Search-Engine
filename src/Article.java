public class Article {
    String id;
    String author;
    String date;
    String cat;
    String title;
    String url;
    String text;

    public Article(String id, String author, String date, String cat, String title, String url, String text) {
        this.id = id;
        this.author = author;
        this.date = date;
        this.cat = cat;
        this.title = title;
        this.url = url;
        this.text = text;
    }


    public String toString() {
        String str = "";
        str += "ID: " + id + "\n";
        str += "Headline: " + title + "\n";
        str += "Category: " + cat + "\n";
        str += "Author: " + author + " (" + date + ")\n";
        str += "URL: " + url;
        return str;
    }
}