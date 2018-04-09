package de.openfiresource.falarm.dagger.module;

import android.arch.lifecycle.ViewModelProvider;

import dagger.Binds;
import dagger.Module;
import de.openfiresource.falarm.dagger.ViewModelFactory;

@Module
public abstract class ViewModelModule {


    @Binds
    abstract ViewModelProvider.Factory bindViewModelFactory(ViewModelFactory factory);
}
