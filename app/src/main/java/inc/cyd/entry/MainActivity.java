package inc.cyd.entry;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import inc.cyd.entry2.BottomEntry;

public class MainActivity extends Activity {
    private Context context = this;
    private BottomEntry bottomEntry;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_main);

        bottomEntry = findViewById(R.id.bottomEntry);
    }
}
