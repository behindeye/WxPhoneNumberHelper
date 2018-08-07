package czc.lazyhelper.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by czc on 2018/8/4.
 */
@Entity
public class SentenceModel {

    @Id
    private String sentence;

    @Generated(hash = 1854914797)
    public SentenceModel(String sentence) {
        this.sentence = sentence;
    }

    @Generated(hash = 1827854529)
    public SentenceModel() {
    }

    public String getSentence() {
        return this.sentence;
    }

    public void setSentence(String sentence) {
        this.sentence = sentence;
    }
}
