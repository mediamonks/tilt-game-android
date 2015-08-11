package temple.core.utils.font;

/**
 * Created by stephan on 16-1-2015.
 */
public enum FontFaceType {
    FUTURA_BOOK("fonts/Poppins-Regular.ttf"),
    FUTURA_MEDIUM("fonts/Poppins-Medium.ttf");

    private final String _assetName;

    FontFaceType(String assetName) {
        _assetName = assetName;
    }

    public String getAssetName() {
        return _assetName;
    }
}
