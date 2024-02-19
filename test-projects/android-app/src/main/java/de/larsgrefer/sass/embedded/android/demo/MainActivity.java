package de.larsgrefer.sass.embedded.android.demo;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import de.larsgrefer.sass.embedded.CompileSuccess;
import de.larsgrefer.sass.embedded.SassCompiler;
import de.larsgrefer.sass.embedded.android.AndroidSassCompilerFactory;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    SassCompiler sassCompiler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        Button compileButton = findViewById(R.id.compile);
        EditText source = findViewById(R.id.source_text);
        EditText target = findViewById(R.id.target_text);

        try (SassCompiler sassCompiler = AndroidSassCompilerFactory.bundled(getApplicationContext())) {
            target.setText(sassCompiler.getVersion().toString());
        } catch (Exception e) {
            Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            Log.e("lib", e.getLocalizedMessage(), e);
        }

        compileButton.setOnClickListener(v -> {

            String sourceString = source.getText().toString();

            try {
                CompileSuccess compileSuccess = sassCompiler.compileScssString(sourceString);
                target.setText(compileSuccess.getCss());

            } catch (Exception e) {

                new MaterialAlertDialogBuilder(this)
                        .setTitle("Error")
                        .setMessage(e.getLocalizedMessage())
                        .show();

                target.setText(e.getLocalizedMessage());
            }

        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (sassCompiler == null) {
            try {
                sassCompiler = AndroidSassCompilerFactory.bundled(this);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            sassCompiler.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            sassCompiler = null;
        }
    }
}
