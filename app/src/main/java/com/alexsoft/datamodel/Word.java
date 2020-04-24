package com.alexsoft.datamodel;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public
class Word {
    private Integer id;
    private String word;
    private String language;
    private String transcription;

    @Override
    public String toString() {
        return word + ( transcription != null && language.equals("ru") ? " [" + transcription + "]" : "" );
    }
}
