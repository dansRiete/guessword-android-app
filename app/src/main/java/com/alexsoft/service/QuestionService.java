package com.alexsoft.service;

import com.alexsoft.datamodel.Question;
import com.alexsoft.datamodel.SuggestionDto;

import java.util.List;

import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by Aleks on 20.03.2017.
 */

public interface QuestionService {

    @GET("question")
    Observable<List<Question>> getAllQuestions();

    @GET("answer/getNext")
    Observable<Question> getNext();

    @PUT("question/{id}")
    Observable<Question> update(@Body Question question, @Path("id") Integer id);

    @POST("question")
    Observable<Question> create(@Body Question question);

    @DELETE("question/{id}")
    Observable<Void> delete(@Path("id") Integer id);

    @POST("answer/right")
    Observable<Question> rightAnswer(@Body Question question);

    @POST("answer/wrong")
    Observable<Question> wrongAnswer(@Body Question question);

    @POST("answer/previousRight")
    Observable<Question> previousRight();

    @POST("answer/previousWrong")
    Observable<Question> previousWrong();

    @POST("answer/actualAnswer")
    Observable<Void> saveActualAnswer(@Query("questionId") Integer questionId, @Query("actualAnswer") String actualAnswer);

    @GET("suggestion")
    Observable<SuggestionDto> getSuggestions(@Query("word") String word, @Query("language") String language);

    @GET("suggestion/check")
    Observable<String> checkTranslation(@Query("en") String en,
                                        @Query("ru") String ru,
                                        @Query("comment") String comment);

}
