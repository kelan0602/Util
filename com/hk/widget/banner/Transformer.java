package com.hk.view.widget.banner;

import java.util.ArrayList;

import com.hk.view.widget.banner.transformer.AccordionTransformer;
import com.hk.view.widget.banner.transformer.BackgroundToForegroundTransformer;
import com.hk.view.widget.banner.transformer.CubeInTransformer;
import com.hk.view.widget.banner.transformer.CubeOutTransformer;
import com.hk.view.widget.banner.transformer.DefaultTransformer;
import com.hk.view.widget.banner.transformer.DepthPageTransformer;
import com.hk.view.widget.banner.transformer.FlipHorizontalTransformer;
import com.hk.view.widget.banner.transformer.FlipVerticalTransformer;
import com.hk.view.widget.banner.transformer.ForegroundToBackgroundTransformer;
import com.hk.view.widget.banner.transformer.NoChangeTranformer;
import com.hk.view.widget.banner.transformer.RotateDownTransformer;
import com.hk.view.widget.banner.transformer.RotateUpTransformer;
import com.hk.view.widget.banner.transformer.ScaleInOutTransformer;
import com.hk.view.widget.banner.transformer.StackTransformer;
import com.hk.view.widget.banner.transformer.TabletTransformer;
import com.hk.view.widget.banner.transformer.ZoomInTransformer;
import com.hk.view.widget.banner.transformer.ZoomOutSlideTransformer;
import com.hk.view.widget.banner.transformer.ZoomOutTranformer;

import android.support.v4.view.ViewPager.PageTransformer;


public class Transformer {	
    public static Class<? extends PageTransformer> Default = DefaultTransformer.class;
    public static Class<? extends PageTransformer> Accordion = AccordionTransformer.class;
    public static Class<? extends PageTransformer> BackgroundToForeground = BackgroundToForegroundTransformer.class;
    public static Class<? extends PageTransformer> ForegroundToBackground = ForegroundToBackgroundTransformer.class;
    public static Class<? extends PageTransformer> CubeIn = CubeInTransformer.class;
    public static Class<? extends PageTransformer> CubeOut = CubeOutTransformer.class;
    public static Class<? extends PageTransformer> DepthPage = DepthPageTransformer.class;
    public static Class<? extends PageTransformer> FlipHorizontal = FlipHorizontalTransformer.class;
    public static Class<? extends PageTransformer> FlipVertical = FlipVerticalTransformer.class;
    public static Class<? extends PageTransformer> RotateDown = RotateDownTransformer.class;
    public static Class<? extends PageTransformer> RotateUp = RotateUpTransformer.class;
    public static Class<? extends PageTransformer> ScaleInOut = ScaleInOutTransformer.class;
    public static Class<? extends PageTransformer> Stack = StackTransformer.class;
    public static Class<? extends PageTransformer> Tablet = TabletTransformer.class;
    public static Class<? extends PageTransformer> ZoomIn = ZoomInTransformer.class;
    public static Class<? extends PageTransformer> ZoomOut = ZoomOutTranformer.class;
    public static Class<? extends PageTransformer> ZoomOutSlide = ZoomOutSlideTransformer.class;
    public static Class<? extends PageTransformer> noChange = NoChangeTranformer.class;
}
