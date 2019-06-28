package fr.arena.monster.monster_arena;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

public class tutoGameActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener, View.OnDragListener {

    ImageView hand_user_1, hand_user_2, hand_user_3, hand_user_4, hand_user_5, cardDetail, user_attack_left, user_attack_right, user_defense, dropZone = null;
    FrameLayout filter;
    String label = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Helper.playTheme(this, "fight");

        setContentView(R.layout.activity_game_board);

        hand_user_1 = (ImageView) findViewById(R.id.hand_user_1);
        hand_user_2 = (ImageView) findViewById(R.id.hand_user_2);
        hand_user_3 = (ImageView) findViewById(R.id.hand_user_3);
        hand_user_4 = (ImageView) findViewById(R.id.hand_user_4);
        hand_user_5 = (ImageView) findViewById(R.id.hand_user_5);
        cardDetail = (ImageView) findViewById(R.id.cardDetail);
        filter = (FrameLayout) findViewById(R.id.filter);
        user_attack_left = (ImageView) findViewById(R.id.left_card_user);
        user_attack_right = (ImageView) findViewById(R.id.right_card_user);
        user_defense = (ImageView) findViewById(R.id.up_card_user);

        hand_user_1.setOnClickListener(this);
        hand_user_2.setOnClickListener(this);
        hand_user_3.setOnClickListener(this);
        hand_user_4.setOnClickListener(this);
        hand_user_5.setOnClickListener(this);
        filter.setOnClickListener(this);

        hand_user_1.setOnLongClickListener(this);
        hand_user_2.setOnLongClickListener(this);
        hand_user_3.setOnLongClickListener(this);
        hand_user_4.setOnLongClickListener(this);
        hand_user_5.setOnLongClickListener(this);

        user_attack_left.setOnDragListener(this);
        user_attack_right.setOnDragListener(this);
        user_defense.setOnDragListener(this);
    }

    @Override
    public void onClick(View v) {
        Drawable source;
        switch (v.getId()) {
            case R.id.hand_user_1:
                source = hand_user_1.getDrawable();
                cardDetail.setImageDrawable(source);
                cardDetail.setVisibility(View.VISIBLE);
                filter.setVisibility(View.VISIBLE);
                break;
            case R.id.hand_user_2:
                source = hand_user_2.getDrawable();
                cardDetail.setImageDrawable(source);
                cardDetail.setVisibility(View.VISIBLE);
                filter.setVisibility(View.VISIBLE);
                break;
            case R.id.hand_user_3:
                source = hand_user_3.getDrawable();
                cardDetail.setImageDrawable(source);
                cardDetail.setVisibility(View.VISIBLE);
                filter.setVisibility(View.VISIBLE);
                break;
            case R.id.hand_user_4:
                source = hand_user_4.getDrawable();
                cardDetail.setImageDrawable(source);
                cardDetail.setVisibility(View.VISIBLE);
                filter.setVisibility(View.VISIBLE);
                break;
            case R.id.hand_user_5:
                source = hand_user_5.getDrawable();
                cardDetail.setImageDrawable(source);
                cardDetail.setVisibility(View.VISIBLE);
                filter.setVisibility(View.VISIBLE);
                break;
            case R.id.filter:
                filter.setVisibility(View.INVISIBLE);
                cardDetail.setVisibility(View.INVISIBLE);
                break;
        }
    }

    @Override
    public boolean onLongClick(View v) {
        View.DragShadowBuilder mShadow = new View.DragShadowBuilder(v);
        ClipData.Item item = new ClipData.Item(v.getTag().toString());
        Toast.makeText(tutoGameActivity.this, item.toString(), Toast.LENGTH_SHORT).show();
        String[] mimeTypes = {ClipDescription.MIMETYPE_TEXT_PLAIN};
        ClipData data = new ClipData(v.getTag().toString(), mimeTypes, item);
        v.startDragAndDrop(data, mShadow, null, 0);
        return false;
    }

    @Override
    public boolean onDrag(View v, DragEvent event) {
        Log.d("onDrag: ", v.toString());

        return dragNDropEvent(event, v);
    }
    public boolean dragNDropEvent(DragEvent event, View v) {
        switch (event.getAction()) {
            case DragEvent.ACTION_DRAG_STARTED:
                label = event.getClipDescription().getLabel().toString();
                v.invalidate();
                return true;

            case DragEvent.ACTION_DRAG_ENTERED:

                v.invalidate();
                return true;

            case DragEvent.ACTION_DRAG_LOCATION:
                return true;

            case DragEvent.ACTION_DRAG_EXITED:

                v.invalidate();
                return true;

            case DragEvent.ACTION_DROP:
                Log.d("dragNDropEvent: drop", event.toString());
                Log.d("dragNDropEvent: drop", v.toString());
                dropZone = (ImageView) v;
                v.invalidate();
                return true;

            case DragEvent.ACTION_DRAG_ENDED:
                if (event.getResult()) {
                    Toast.makeText(tutoGameActivity.this, "Awesome!", Toast.LENGTH_SHORT).show();
                    if (label == null)
                        return true;
                    Log.d("dragNDropEvent: ", label);
                    Drawable source;
                    switch (label) {
                        case "hand_user_1":
                            source = hand_user_1.getDrawable();
                            dropZone.setImageDrawable(source);
                            hand_user_1.setVisibility(View.INVISIBLE);
                            break;
                        case "hand_user_2":
                            source = hand_user_2.getDrawable();
                            dropZone.setImageDrawable(source);
                            hand_user_2.setVisibility(View.INVISIBLE);
                            break;
                        case "hand_user_3":
                            source = hand_user_3.getDrawable();
                            dropZone.setImageDrawable(source);
                            hand_user_3.setVisibility(View.INVISIBLE);
                            break;
                        case "hand_user_4":
                            source = hand_user_4.getDrawable();
                            dropZone.setImageDrawable(source);
                            hand_user_4.setVisibility(View.INVISIBLE);
                            break;
                        case "hand_user_5":
                            source = hand_user_5.getDrawable();
                            dropZone.setImageDrawable(source);
                            hand_user_5.setVisibility(View.INVISIBLE);
                            break;
                    }

                } else {
                    Toast.makeText(tutoGameActivity.this, "Aw Snap! Try dropping it again", Toast.LENGTH_SHORT).show();
                }
                return true;

            default:
                return false;
        }
    }
}
