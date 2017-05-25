package com.base.viewinject;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import android.util.Log;

public class ViewUtils {
	private static final String TAG = "ViewUtils";
	private static final String METHOD_FIND_VIEW_BY_ID = "findViewById";
	/**
	 * 
	 * @param view View,Activity
	 */
	public static void inject(Object view)
	{
		inject(view, view);

	}
	
	/**
	 * 
	 * @param holder object holder the fields to be inject
	 * @param view View,Activity
	 */
	public static void inject(Object holder, Object view) {
		Class<?> clazzHolder = holder.getClass();
		Class<?> clazzView = view.getClass();
		Field[] fields = clazzHolder.getDeclaredFields();
		// 遍历所有成员变量
		for (Field field : fields)
		{
			
			ViewInject viewInjectAnnotation = field
					.getAnnotation(ViewInject.class);
			if (viewInjectAnnotation != null)
			{
				int viewId = viewInjectAnnotation.value();
				if (viewId > 0)
				{
					// 初始化View
					try
					{
						Method method = clazzView.getMethod(METHOD_FIND_VIEW_BY_ID,
								int.class);
						Object resView = method.invoke(view, viewId);
						field.setAccessible(true);
						field.set(holder, resView);
					} catch (Exception e)
					{
						e.printStackTrace();
					}

				} else {
					Log.e(TAG, "error id = " + viewId);
				}
			}

		}
	}
}
