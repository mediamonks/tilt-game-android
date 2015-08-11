package com.mediamonks.googleflip.pages.game_flow.singleplayer.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;

import com.mediamonks.googleflip.R;
import com.mediamonks.googleflip.data.vo.LevelResultVO;
import com.mediamonks.googleflip.data.vo.LevelVO;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import temple.core.ui.CustomTextView;

/**
 * Adapter for level selection list
 */
public class LevelAdapter extends BaseAdapter {
    private static final String TAG = LevelAdapter.class.getSimpleName();

    private List<LevelResultVO> _results;
    private Context _context;
    private List<LevelVO> _levels;
    private int _rowHeight = 0;
    private LayoutInflater _inflater;

    public LevelAdapter(Context context, List<LevelVO> levels, int rowHeight, List<LevelResultVO> results) {
        _context = context;
        _levels = levels;
        _rowHeight = rowHeight;
        _results = results;

        _inflater = (LayoutInflater) _context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return _levels.size();
    }

    @Override
    public LevelVO getItem(int position) {
        return _levels.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private LevelResultVO getResultData(int position) {
        if(_results != null) {
            for(LevelResultVO result : _results) {
                if(result.id == position) {
                    return result;
                }
            }
        }

        return null;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder holder;

        if (view == null) {
            view = _inflater.inflate(R.layout.level_list_item, parent, false);
            holder = new ViewHolder(view);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        holder.update(view, getItem(position), position, getResultData(position));

        return view;
    }

    protected class ViewHolder {
        @InjectView(R.id.label)
        protected CustomTextView _labelText;
        @InjectView(R.id.record_label)
        protected CustomTextView _recordText;
        @InjectView(R.id.list_item_container)
        protected LinearLayout _container;

        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }

        private void update (View view, LevelVO levelVO, int position, LevelResultVO resultData) {
            boolean isTutorial = (position == 0);

            _labelText.setText(isTutorial ? _context.getString(R.string.tutorial) : _context.getString(R.string.level_name, position));

            ViewGroup.LayoutParams params = _container.getLayoutParams();
            if (params == null) {
                _container.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, _rowHeight));
            } else {
                params.height = _rowHeight;
            }

            boolean unlocked = isTutorial || levelVO.unlocked;

            view.setClickable(!unlocked);

            view.setBackgroundColor(_context.getResources().getColor(isTutorial ? R.color.yellow : R.color.transparent));
            _labelText.setTextColor(_context.getResources().getColor(unlocked ? R.color.white : R.color.white_20));

            if(resultData != null && resultData.success) {
                _recordText.setText(String.format("%.01f",resultData.seconds) + "s");
                _recordText.setVisibility(View.VISIBLE);
            } else {
                _recordText.setVisibility(View.GONE);
            }
        }
    }
}
