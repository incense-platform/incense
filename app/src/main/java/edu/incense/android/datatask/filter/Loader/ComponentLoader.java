package edu.incense.android.datatask.filter.Loader;

// InCense references.
import edu.incense.android.datatask.filter.DataFilter;
import edu.incense.android.datatask.data.Data;

// Android references.
import dalvik.system.DexClassLoader;
import dalvik.system.DexFile;
import android.content.Context;
import android.util.Log;

// Java references;
import java.io.File;
import java.lang.reflect.Constructor;

/**
 * Created by xilef on 3/9/2016.
 */
public class ComponentLoader {
    // Constants.
    private static final String TAG = "ComponentLoader";
    private static final String COMPONENTS_FOLDER = "components";
    private static final String COMPONENTS_DEX_FOLDER = "components/dex";

    // Fields
    private String componentName;
    private Context context;
    private File incenseDir;

    public String getComponentName(){
        return this.componentName;
    }

    public void setComponentName(String componentName){
        this.componentName = componentName;
    }

    public ComponentLoader(Context context, String componentName){
        this.componentName = componentName;
        this.context = context;
        this.incenseDir = context.getFilesDir();
    }

    /**
     * Creates an instance of the component defined by de provided name in the constructor of this clas
     *
     * @return Return an instance of the component using the parent DataFilter type.
     */
    public DataFilter createInstanceOfComponent(){
        File component = getComponentFile();
        if (component == null){
            // Try to get component from repository.
        }

        DexClassLoader dcl = new DexClassLoader(component.getAbsolutePath(), this.incenseDir +
                "/" + COMPONENTS_DEX_FOLDER, null, this.getClass().getClassLoader());

        try {
            Class<?> tmpClass = dcl.loadClass("edu.incense.android.datatask.filter." + this.componentName);
//            Class<?> tmpClass1 = dcl.loadClass("edu.incense.android.datatask.data.WifiNetworkCountData");
//            Data tmp = tmpClass1.asSubclass(Data.class).newInstance();
            Class<? extends DataFilter> finalClass = tmpClass.asSubclass(DataFilter.class);
            //Constructor<?> ctor = getParameterlessConstructor(finalClass.getConstructors());
            return  finalClass.newInstance();
        }
        catch(Exception e){
            Log.e(TAG, e.getMessage());
        }

        return null;
    }

    private Constructor<?> getParameterlessConstructor(Constructor<?>[] constructors){
        for (Constructor<?> c : constructors){
            if (c.getParameterTypes().length == 0)
                return c;
        }

        return null;
    }

    private File getComponentFile(){
        if (!CheckComponentsFolderStructure(this.context))
            return null;
        File componentFile =  new File (this.incenseDir, COMPONENTS_FOLDER + "/" + this.componentName + ".jar");

        if (componentFile.exists())
            return componentFile;

        return null;
    }

    /*
    * Checks whether the components directory structure exists; if it does not exists then this
    * functions creates it.
    *
    * Returns true if the folder exists o could be created, false otherwise.
    */
    public static boolean CheckComponentsFolderStructure(Context context){
        File componentDexDir = new File(context.getFilesDir(), COMPONENTS_DEX_FOLDER);

        if (!componentDexDir.exists())
            return componentDexDir.mkdirs();

        return true;
    }


}
