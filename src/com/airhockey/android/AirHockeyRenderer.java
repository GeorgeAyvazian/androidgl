package com.airhockey.android;

import android.content.Context;
import android.content.res.Resources;
import android.opengl.GLSurfaceView;
import com.airhockey.android.util.LoggerConfig;
import com.airhockey.android.util.ShaderHelper;
import com.airhockey.android.util.TextResourceReader;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_FRAGMENT_SHADER;
import static android.opengl.GLES20.GL_LINES;
import static android.opengl.GLES20.GL_POINTS;
import static android.opengl.GLES20.GL_TRIANGLES;
import static android.opengl.GLES20.GL_VERTEX_SHADER;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniform4f;
import static android.opengl.GLES20.glUseProgram;
import static android.opengl.GLES20.glVertexAttribPointer;
import static android.opengl.GLES20.glViewport;

public class AirHockeyRenderer implements GLSurfaceView.Renderer {

    private static final int BYTES_PER_FLOAT = 4;
    private static final String U_COLOR = "u_Color";
    private static final String A_POSITION = "a_Position";
    private final FloatBuffer vertexData;
    private final Context context;
    private int program;
    private int uColorLocation;
    private int aPositionLocation;
    private static final int POSITION_COMPONENT_COUNT = 2;


    public AirHockeyRenderer(Context context) {
        this.context = context;
        float[] tableVertices = {
                // Triangle 1
                -0.5f, -0.5f,
                 0.5f, 0.5f,
                -0.5f, 0.5f,

                // Triangle 2
                -0.5f, -0.5f,
                 0.5f, -0.5f,
                 0.5f, 0.5f,

                // Line 1
                -0.5f, 0.0f,
                 0.5f, 0.0f,

                // Mallets
                0.0f, -0.25f,
                0.0f, 0.25f,

                // Puck
                0.0f, 0.0f
        };
        vertexData = ByteBuffer.allocateDirect(tableVertices.length * BYTES_PER_FLOAT).order(ByteOrder.nativeOrder()).asFloatBuffer();
        vertexData.put(tableVertices);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        final String vertexShaderSource;
        final String fragmentShaderSource;
        try {
            final Resources resources = context.getResources();
            vertexShaderSource = TextResourceReader.readTextFileFromResource(resources, R.raw.simple_vertex_shader);
            fragmentShaderSource = TextResourceReader.readTextFileFromResource(resources, R.raw.simple_fragment_shader);
        } catch (IOException ignored) {
            return;
        }
        final int vertexShader = ShaderHelper.compileShader(GL_VERTEX_SHADER, vertexShaderSource);
        final int fragmentShader = ShaderHelper.compileShader(GL_FRAGMENT_SHADER, fragmentShaderSource);
        program = ShaderHelper.linkProgram(vertexShader, fragmentShader);
        if (LoggerConfig.ON) {
            ShaderHelper.validateProgram(program);
        }
        glUseProgram(program);
        uColorLocation = glGetUniformLocation(program, U_COLOR);
        aPositionLocation = glGetAttribLocation(program, A_POSITION);
        vertexData.position(0);
        glVertexAttribPointer(aPositionLocation, POSITION_COMPONENT_COUNT, GL_FLOAT, false, 0, vertexData);
        glEnableVertexAttribArray(aPositionLocation);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        glViewport(0, 0, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        glClear(GL_COLOR_BUFFER_BIT);
        // Draw the two triangles
        glUniform4f(uColorLocation, 1.0f, 1.0f, 1.0f, 1.0f);
        glDrawArrays(GL_TRIANGLES, 0, 6);
        // then draw the dividing line
        glUniform4f(uColorLocation, 1.0f, 0.0f, 0.0f, 1.0f);
        glDrawArrays(GL_LINES, 6, 2);
        // draw the first mallet blue
        glUniform4f(uColorLocation, 0.0f, 0.0f, 1.0f, 1.0f);
        glDrawArrays(GL_POINTS, 8, 1);
        // draw the second mallet red
        glUniform4f(uColorLocation, 1.0f, 0.0f, 0.0f, 1.0f);
        glDrawArrays(GL_POINTS, 9, 1);
        // draw the puck
        glUniform4f(uColorLocation, 0.0f, 0.0f, 0.0f, 1.0f);
        glDrawArrays(GL_POINTS, 10, 1);
    }
}
