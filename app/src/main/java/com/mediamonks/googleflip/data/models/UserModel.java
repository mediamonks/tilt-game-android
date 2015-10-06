package com.mediamonks.googleflip.data.models;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

import com.mediamonks.googleflip.data.constants.LevelColor;
import com.mediamonks.googleflip.data.constants.LevelResult;
import com.mediamonks.googleflip.data.constants.PrefKeys;
import com.mediamonks.googleflip.data.vo.LevelResultVO;
import com.mediamonks.googleflip.data.vo.LevelVO;
import com.pixplicity.easyprefs.library.Prefs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

/**
 * Model for storing user related data
 */
public class UserModel {
    private static final String TAG = UserModel.class.getSimpleName();

    private Long _selectedLevelId = 0L;
    private int _tutorialLevel = 0;
    private List<LevelResultVO> _levelResults;
    private ArrayList<LevelColor> _backgroundColors;
    private LevelColor _currentBackgroundColor;
    private LevelColor _nextBackgroundColor;
    private Context _context;
    private List<LevelVO> _levels;
    private int _selectedLevelIndex = 0;
    private boolean _isDataLoaded;

    public UserModel(Context context) {
        _context = context;
    }

    public void setLevels(List<LevelVO> levels) {
        _levels = levels;
    }

    public void setLevelResults(List<LevelResultVO> results) {
        _levelResults = results;
    }

    public void selectLevelById(Long selectedLevelId) {
        _selectedLevelId = selectedLevelId;

        int index = 0;
        for (LevelVO levelVO : _levels) {
            if (levelVO.id.equals(selectedLevelId)) {
                _selectedLevelIndex = index;
                break;
            }

            index++;
        }
    }

    public void selectLevelByIndex (int index) {
        _selectedLevelIndex = index;
        _selectedLevelId = _levels.get(index).id;
    }

    public int getTutorialLevel() {
        return _tutorialLevel;
    }

    public void setTutorialLevel(int tutorialLevel) {
        _tutorialLevel = tutorialLevel;
    }

    public void selectNextLockedLevel() {
        _selectedLevelIndex = 0;

        for (LevelVO levelVO : _levels) {
            if (!levelVO.unlocked) {
                _selectedLevelId = levelVO.id;
                return;
            }

            _selectedLevelIndex++;
        }
    }

    public boolean hasNextLevel () {
        return _levels != null && _selectedLevelIndex < _levels.size() - 1;
    }

    public void selectNextLevel() {
        selectLevelByIndex(_selectedLevelIndex + 1);
    }

    public Long getSelectedLevelId() {
        return _selectedLevelId;
    }

    public int getSelectedLevelIndex() {
        return _selectedLevelIndex;
    }

    public LevelVO getSelectedLevel() {
        return getLevelById(_selectedLevelId);
    }

    /**
     * @return true if the result was an improvement over the previous result for this level
     */
    public LevelResult updateLevelResult(LevelResultVO newResultVO) {
        LevelResult levelResult;

        if (newResultVO.success && hasNextLevel()) {
            // unlock next level
            int index = _selectedLevelIndex + 1;
            unlockLevel(index);
        }

        LevelResultVO currentResultVO = getLevelResultById(newResultVO.id);
        assert currentResultVO != null;

        if (!currentResultVO.success) {
            levelResult = LevelResult.NEW;
        } else {
            levelResult = (newResultVO.seconds < currentResultVO.seconds) ? LevelResult.BETTER : LevelResult.WORSE;
        }

        // store new result if improved
        if (!currentResultVO.success || (newResultVO.seconds < currentResultVO.seconds)) {
            // update memory storage
            currentResultVO.copyFrom(newResultVO);

            // update database
            cupboard().withContext(_context).put(LevelResultVO.URI, LevelResultVO.class, currentResultVO);
        }

        return levelResult;
    }

    public void unlockLevel(int index) {
        LevelVO levelVO = _levels.get(index);
        levelVO.unlocked = true;

        ContentValues contentValues = new ContentValues();
        contentValues.put(LevelVO.FIELD_UNLOCKED, true);

        cupboard().withContext(_context).update(LevelVO.URI, contentValues, LevelVO.FIELD_ID + "='" + levelVO.id + "'");
    }

    private LevelVO getLevelById(Long levelId) {
        for (LevelVO levelVO : _levels) {
            if (levelVO.id.equals(levelId)) {
                return levelVO;
            }
        }
        return null;
    }

    private LevelResultVO getLevelResultById(Long levelId) {
        for (LevelResultVO levelResultVO : _levelResults) {
            if (levelResultVO.id.equals(levelId)) {
                return levelResultVO;
            }
        }
        return null;
    }

    public LevelResultVO getResultForLevel(Long levelId) {
        for (LevelResultVO resultVO : _levelResults) {
            if (resultVO.id.equals(levelId)) {
                return resultVO;
            }
        }

        return null;
    }

    public List<LevelResultVO> getLevelResults() {
        return _levelResults;
    }

    public LevelColor randomizeBackgroundColor() {
        if (_backgroundColors == null) {
            _backgroundColors = new ArrayList<>();
            _nextBackgroundColor = LevelColor.BLUE;
        }

        if (_backgroundColors.size() == 0) {
            _backgroundColors.clear();
            _backgroundColors.add(LevelColor.BLUE);
            _backgroundColors.add(LevelColor.CYAN);
            _backgroundColors.add(LevelColor.PURPLE);
            _backgroundColors.add(LevelColor.PINK);

            do {
                Collections.shuffle(_backgroundColors);
            } while (_backgroundColors.get(0).equals(_nextBackgroundColor));
        }

        _currentBackgroundColor = _nextBackgroundColor;
        _nextBackgroundColor = _backgroundColors.remove(0);

        return _currentBackgroundColor;
    }

    public LevelColor getCurrentBackgroundColor() {
        if (_currentBackgroundColor == null) {
            randomizeBackgroundColor();
        }

        return _currentBackgroundColor;
    }

    public LevelColor getNextBackgroundColor() {
        return _nextBackgroundColor;
    }

    public List<LevelVO> getLevels() {
        return _levels;
    }

    public boolean isDataLoaded() {
        return _isDataLoaded;
    }

    public void setIsDataLoaded(boolean isDataLoaded) {
        _isDataLoaded = isDataLoaded;
    }
}
