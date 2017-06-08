package project.min.school.schoolapp;

import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import project.min.school.schoolapp.databinding.ActivityArticlesDataBindingBinding;

public class ArticlesDataBinding extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_articles_data_binding);


        //set ContentView
        ActivityArticlesDataBindingBinding binding = DataBindingUtil.setContentView(this,R.layout.activity_articles_data_binding);

        //Create News Feed object
        NewsFeed newsFeed = new NewsFeed("article","url","content");
        //Now set the object
        binding.setNewsFeed(newsFeed);

    }
}
