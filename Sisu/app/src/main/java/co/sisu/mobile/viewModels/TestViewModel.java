package co.sisu.mobile.viewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class TestViewModel extends androidx.lifecycle.ViewModel {
    private final MutableLiveData<String> selected = new MutableLiveData<>();

    public void select(String item) {
        selected.setValue(item);
    }

    public LiveData<String> getSelected() {
        return selected;
    }
}
