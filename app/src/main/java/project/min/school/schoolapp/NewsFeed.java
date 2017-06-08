package project.min.school.schoolapp;

/**
 * Created by Abdulqani on 6/1/2017.
 */

public class NewsFeed {
    private String title;
    private String url;
    private String content;

    public NewsFeed(String title, String url, String content) {
        this.title = title;
        this.url = url;
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    public String getContent() {
        return content;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
