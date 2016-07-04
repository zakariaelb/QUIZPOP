package com.education.popquiz;

import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("ResourceType")
public class MainActivity extends AppCompatActivity {

    LinearLayout quizCard; // the layout containing the question
    TextView questionNumberTxtView; // TextView indicating the question number
    TextView questionTxtView; // TextView containing the question

    RadioGroup optRadioGrp; // options with only one answer
    List<CheckBox> optCheckGrp; // options with multiple answer
    EditText answerEditTxt; // an input text box

    List<Question> questionList; // Array holding the questions

    int questionIndex; // keeps rack of the question index
    int score; // the score

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // initialize the questions
        questionList = parseQuestions();

        // the submit button
        final Button submitBtn = (Button) findViewById(R.id.submit_btn);
        if (submitBtn != null) {
            // onCLick,
            // checks if the user input is correct
            // move on to the next question
            submitBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                // check if the user input is correct
                if (questionIndex < questionList.size()) checkAnswer(questionIndex);
                questionIndex++;
                // moves to the next question
                if (questionIndex < questionList.size()) loadQuestion(questionIndex);
                else { // shows the score
                    questionNumberTxtView.setText(R.string.done_msg);
                    questionTxtView.setText("Your score is: " + score + "/" + questionList.size());
                    clearOptions();
                    submitBtn.setEnabled(false);
                    Toast.makeText(getApplicationContext(),
                            "Result: " + score + "/" + questionList.size() + " answered",
                            Toast.LENGTH_SHORT).show();
                }
                }
            });
        }

        // initialize some layout widgets
        quizCard = (LinearLayout) findViewById(R.id.quiz_panel);
        questionNumberTxtView = (TextView) findViewById(R.id.quiz_number);
        questionTxtView = (TextView) findViewById(R.id.quiz_question);
        // initialize the RadioGroup
        optRadioGrp = new RadioGroup(this);
        optRadioGrp.setOrientation(RadioGroup.VERTICAL);
        // initialize the CheckGroup
        optCheckGrp = new ArrayList<CheckBox>();
        // initialize the EditText
        answerEditTxt = new EditText(this);
        answerEditTxt.setHint(R.string.input_box_hint);
        answerEditTxt.setMinLines(1);
        answerEditTxt.setMaxLines(3);

        // set to Zero
        questionIndex = 0;
        score = 0;
        // load the first question
        loadQuestion(questionIndex);

    }

    /**
     * Parse the questions from string.xml
     * @return An ArrayList of questions
     */
    private ArrayList<Question> parseQuestions() {
        int questionIdx = 0;
        int typeIdx = 1;
        int answerIdx = 2;
        int optionsIdx = 3;
        ArrayList<Question> questionArrayList = new ArrayList<Question>();

        // read the questions from the xml
        TypedArray questionArr = getResources().obtainTypedArray(R.array.questions);

        // iterate and parse
        for(int i = 0; i < questionArr.length(); i++) {
            int resId = questionArr.getResourceId(i, -1);
            TypedArray questionTArr = getResources().obtainTypedArray(resId);
            TypedArray optionsTArr = getResources()
                    .obtainTypedArray(questionTArr.getResourceId(optionsIdx, -1));

            String question = questionTArr.getString(questionIdx); // the question
            int type = Integer.valueOf(questionTArr.getString(typeIdx)); // question type
            String answer = questionTArr.getString(answerIdx); // the answer
            ArrayList<String> options = new ArrayList<String>(); // options
            for(int j = 0; j < optionsTArr.length(); j++) {
                options.add(optionsTArr.getString(j));
            }
            Question q = new Question(question, answer, type, options); // instantiate a question object
            questionArrayList.add(q); // add to the ArrayList
        }
        return questionArrayList;
    }

    /**
     * Loads the question
     * @param index The question index
     */
    private void loadQuestion(int index) {
        Question question = questionList.get(index);

        // update the question number display
        questionNumberTxtView.setText("Q"+(index+1));

        // display the question
        questionTxtView.setText(question.getQuestion());

        // attach the appropriate option type
        switch (question.getType()) {
            case 1:
                attachRadioGroup(question.getOptions());
                break;
            case 2:
                attachCheckGroup(question.getOptions());
                break;
            case 3:
                attachInputBox();
        }
    }

    /**
     * Checks if the answer is correct
     * @param index The question index
     */
    private void checkAnswer(int index) {
        Question question = questionList.get(index);

        String correctAnswer = question.getAnswer(); // gets the correct answer from the database
        String userAnswer = "";

        // parse the user input
        switch (question.getType()) {
            case 1:
                userAnswer = ""+optRadioGrp.getCheckedRadioButtonId();
                break;
            case 2:
                for (CheckBox cb : optCheckGrp) {
                    if(cb.isChecked()) {
                        userAnswer += cb.getId() + ",";
                    }
                }
                break;
            case 3:
                userAnswer = answerEditTxt.getText().toString().toLowerCase();
        }

        // compare the user's answer to the correct answer and respond accordingly
        if (userAnswer.equals(correctAnswer)) {
            Toast.makeText(getApplicationContext(),
                    getResources().getText(R.string.correct_msg),Toast.LENGTH_SHORT).show();
            score++;
        }
        else {
            Toast.makeText(getApplicationContext(),
                    getResources().getText(R.string.wrong_msg),Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * Attach a RadioGroup type options
     * @param options The choices to be displayed
     */
    private void attachRadioGroup(List<String> options) {
        clearOptions();
        optRadioGrp.removeAllViews();

        for(int i=0; i<options.size(); i++){
            RadioButton rb = new RadioButton(this);
            rb.setText(options.get(i));
            rb.setId(i);
            optRadioGrp.addView(rb);
        }
        quizCard.addView(optRadioGrp);
    }

    /**
     * Attach a CheckGroup type options
     * @param options The choices to be displayed
     */
    private void attachCheckGroup(List<String> options) {
        clearOptions();
        optCheckGrp.clear();

        for(int i=0; i<options.size(); i++){
            CheckBox cb = new CheckBox(this);
            cb.setText(options.get(i));
            cb.setId(i);
            optCheckGrp.add(cb);
            quizCard.addView(cb);
        }
    }

    /**
     * Attach an InputBox type answer
     */
    private void attachInputBox() {
        clearOptions();
        answerEditTxt.setText("");
        quizCard.addView(answerEditTxt);
    }

    /**
     * Removes the previous options
     */
    private void clearOptions() {
        quizCard.removeView(optRadioGrp);

        for(CheckBox cb : optCheckGrp) {
            quizCard.removeView(cb);
        }

        quizCard.removeView(answerEditTxt);
    }
}
