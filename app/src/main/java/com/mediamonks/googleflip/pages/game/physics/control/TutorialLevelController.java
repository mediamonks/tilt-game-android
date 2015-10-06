package com.mediamonks.googleflip.pages.game.physics.control;

import android.graphics.Typeface;
import android.util.Log;

import com.badlogic.gdx.math.Vector2;
import com.mediamonks.googleflip.GoogleFlipGameApplication;
import com.mediamonks.googleflip.R;
import com.mediamonks.googleflip.pages.game.physics.levels.GameLevel;

import org.andengine.engine.Engine;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.extension.physics.box2d.util.Vector2Pool;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.util.adt.align.HorizontalAlign;
import org.andengine.util.adt.color.Color;

import temple.core.utils.font.FontFaceType;

/**
 * GameLevelController implementation for tutorial level
 */
public class TutorialLevelController extends GameLevelController {
    private static final String TAG = TutorialLevelController.class.getSimpleName();

    private Text _explanationText1;
    private Text _explanationText2;

    @Override
    public void init(GameLevel gameLevel, PhysicsWorld physicsWorld, Engine engine) {
        super.init(gameLevel, physicsWorld, engine);

        float camHeight = engine.getCamera().getHeight();
        float originalHeight = 1920;
        float heightScale = camHeight / originalHeight;

        Typeface typeFace = Typeface.createFromAsset(GoogleFlipGameApplication.sContext.getAssets(), FontFaceType.FUTURA_BOOK.getAssetName());

        Font explanationFont = FontFactory.create(_engine.getFontManager(), _engine.getTextureManager(), 512, 768, TextureOptions.BILINEAR, typeFace, (int) 20 * GoogleFlipGameApplication.sContext.getResources().getDisplayMetrics().density, Color.WHITE_ABGR_PACKED_INT);
        explanationFont.load();

        _engine.getFontManager().loadFont(explanationFont);

        Vector2 textPoint = Vector2Pool.obtain(_engine.getCamera().getWidth() / 2, _engine.getCamera().getHeight() * .70f);
        String explanation1, explanation2;

        switch (GoogleFlipGameApplication.getUserModel().getTutorialLevel()) {
            case 1:
                explanation1 = GoogleFlipGameApplication.sContext.getResources().getString(R.string.tutorial_2a);
                explanation2 = GoogleFlipGameApplication.sContext.getResources().getString(R.string.tutorial_2b);
                break;
            case 2:
                explanation1 = GoogleFlipGameApplication.sContext.getResources().getString(R.string.tutorial_3);
                explanation2 = "";
                break;
            default:
                explanation1 = GoogleFlipGameApplication.sContext.getResources().getString(R.string.tutorial_1a);
                explanation2 = GoogleFlipGameApplication.sContext.getResources().getString(R.string.tutorial_1b);
				break;
        }

        _explanationText1 = new Text(textPoint.x, textPoint.y, explanationFont, explanation1, new TextOptions(HorizontalAlign.CENTER), _engine.getVertexBufferObjectManager());
        _explanationText2 = new Text(textPoint.x, textPoint.y - (_explanationText1.getHeight()), explanationFont, explanation2, new TextOptions(HorizontalAlign.CENTER), _engine.getVertexBufferObjectManager());

        _engine.getScene().attachChild(_explanationText1);
        _engine.getScene().attachChild(_explanationText2);
    }

    @Override
    public void dispose() {
        if(_explanationText1 != null) {
			_explanationText1.detachSelf();
            _explanationText1.dispose();
            _explanationText1 = null;
        }
        if(_explanationText2 != null) {
			_explanationText2.detachSelf();
            _explanationText2.dispose();
            _explanationText2 = null;
        }

		super.dispose();
    }
}
