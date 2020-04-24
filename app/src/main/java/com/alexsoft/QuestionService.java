package com.alexsoft;

import com.alexsoft.datamodel.Question;

import java.util.List;

import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import rx.Observable;

/**
 * Created by Aleks on 20.03.2017.
 */

interface QuestionService {

    @GET("question")
    Observable<List<Question>> getAllQuestions();

    @GET("question/getNext")
    Observable<Question> getNext();

    @POST("question/right")
    Observable<Question> rightAnswer(@Body Question question);

    @POST("question/wrong")
    Observable<Question> wrongAnswer(@Body Question question);

}
