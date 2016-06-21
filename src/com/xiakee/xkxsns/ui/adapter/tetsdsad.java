package com.xiakee.xkxsns.ui.adapter;
//package com.xiakee.xkxsns.adapter;
//
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.AbsListView;
//import android.widget.ImageView;
//
//public class tetsdsad {
//	
//	public class tetsdsad extends ArrayAdapter<string> {
//	    private GridView mGridView;
//	    //图片缓存类
//	    private LruCache<string, bitmap=""> mLruCache;
//	    //GridView中可见的第一张图片的下标
//	    private int mFirstVisibleItem;
//	    //GridView中可见的图片的数量
//	    private int mVisibleItemCount;
//	    //记录是否是第一次进入该界面
//	    private boolean isFirstEnterThisActivity = true;
//	 
//	    public GridViewAdapter(Context context, int textViewResourceId,String[] objects, GridView gridView) {
//	        super(context, textViewResourceId, objects);
//	        mGridView = gridView;
//	        mGridView.setOnScrollListener(new ScrollListenerImpl());
//	        //应用程序最大可用内存
//	        int maxMemory = (int) Runtime.getRuntime().maxMemory();
//	        //设置图片缓存大小为maxMemory的1/3
//	        int cacheSize = maxMemory/3;
//	         
//	        mLruCache = new LruCache<string, bitmap="">(cacheSize) {
//	            @Override
//	            protected int sizeOf(String key, Bitmap bitmap) {
//	                return bitmap.getRowBytes() * bitmap.getHeight();
//	            }
//	        };
//	         
//	    }
//	 
//	    @Override
//	    public View getView(int position, View convertView, ViewGroup parent) {
//	        String path = getItem(position);
//	        View view;
//	        if (convertView == null) {
//	            view = LayoutInflater.from(getContext()).inflate(R.layout.gridview_item, null);
//	        } else {
//	            view = convertView;
//	        }
//	        ImageView imageView = (ImageView) view.findViewById(R.id.imageView);
//	        //为该ImageView设置一个Tag,防止图片错位
//	        imageView.setTag(path);
//	        //为该ImageView设置显示的图片
//	        setImageForImageView(path, imageView);
//	        return view;
//	    }
//	 
//	    /**
//	     * 为ImageView设置图片(Image)
//	     * 1 从缓存中获取图片
//	     * 2 若图片不在缓存中则为其设置默认图片
//	     */
//	    private void setImageForImageView(String imagePath, ImageView imageView) {
//	        Bitmap bitmap = getBitmapFromLruCache(imagePath);
//	        if (bitmap != null) {
//	            imageView.setImageBitmap(bitmap);
//	        } else {
//	            imageView.setImageResource(R.drawable.default_image);
//	        }
//	    }
//	 
//	    /**
//	     * 将图片存储到LruCache
//	     */
//	    public void addBitmapToLruCache(String key, Bitmap bitmap) {
//	        if (getBitmapFromLruCache(key) == null) {
//	            mLruCache.put(key, bitmap);
//	        }
//	    }
//	 
//	    /**
//	     * 从LruCache缓存获取图片
//	     */
//	    public Bitmap getBitmapFromLruCache(String key) {
//	        return mLruCache.get(key);
//	    }
//	 
//	     
//	 
//	   /**
//	    * 为GridView的item加载图片
//	    * @param firstVisibleItem GridView中可见的第一张图片的下标
//	    * @param visibleItemCount GridView中可见的图片的数量
//	    */
//	    private void loadBitmaps(int firstVisibleItem, int visibleItemCount) {
//	        try {
//	            for (int i = firstVisibleItem; i < firstVisibleItem + visibleItemCount; i++) {
//	                String imagePath = ImagesPath.IMAGES_PATH[i];
//	                Bitmap bitmap = getBitmapFromLruCache(imagePath);
//	                if (bitmap == null) {
//	                    System.out.println("--->XXXXX 图片"+imagePath+"不在缓存中"+",所以从SDCard读取");
//	                    bitmap=BitmapFactory.decodeFile(imagePath);
//	                    ImageView imageView = (ImageView) mGridView.findViewWithTag(imagePath);
//	                    if (imageView != null && bitmap != null) {
//	                        imageView.setImageBitmap(bitmap);
//	                    }
//	                    //将从SDCard读取的图片添加到LruCache中
//	                    addBitmapToLruCache(imagePath, bitmap);
//	                } else {
//	                    System.out.println("--->OOOOO 图片在缓存中="+imagePath+",从缓存中取出即可");
//	                    //依据Tag找到对应的ImageView显示图片
//	                    ImageView imageView = (ImageView) mGridView.findViewWithTag(imagePath);
//	                    if (imageView != null && bitmap != null) {
//	                        imageView.setImageBitmap(bitmap);
//	                    }
//	                }
//	            }
//	        } catch (Exception e) {
//	            e.printStackTrace();
//	        }
//	    }
//	 
//	     
//	     
//	    private class ScrollListenerImpl implements OnScrollListener{
//	        /**
//	         * 通过onScrollStateChanged获知:每次GridView停止滑动时加载图片
//	         * 但是存在一个特殊情况:
//	         * 当第一次入应用的时候,此时并没有滑动屏幕的操作即不会调用onScrollStateChanged,但应该加载图片.
//	         * 所以在此处做一个特殊的处理.
//	         * 即代码:
//	         * if (isFirstEnterThisActivity && visibleItemCount > 0) {
//	         *      loadBitmaps(firstVisibleItem, visibleItemCount);
//	         *      isFirstEnterThisActivity = false;
//	         *    }
//	         *    
//	         * ------------------------------------------------------------
//	         * 
//	         * 其余的都是正常情况.
//	         * 所以我们需要不断保存:firstVisibleItem和visibleItemCount
//	         * 从而便于中在onScrollStateChanged()判断当停止滑动时加载图片
//	         * 
//	         */
//	        @Override
//	        public void onScroll(AbsListView view, int firstVisibleItem,int visibleItemCount, int totalItemCount) {
//	            mFirstVisibleItem = firstVisibleItem;
//	            mVisibleItemCount = visibleItemCount;
//	            if (isFirstEnterThisActivity && visibleItemCount > 0) {
//	                System.out.println("---> 第一次进入该界面");
//	                loadBitmaps(firstVisibleItem, visibleItemCount);
//	                isFirstEnterThisActivity = false;
//	            }
//	        }
//	         
//	        /**
//	         *  GridView停止滑动时下载图片
//	         *  其余情况下取消所有正在下载或者等待下载的任务
//	         */
//	        @Override
//	        public void onScrollStateChanged(AbsListView view, int scrollState) {
//	            if (scrollState == SCROLL_STATE_IDLE) {
//	                System.out.println("---> GridView停止滑动  mFirstVisibleItem="+mFirstVisibleItem+",mVisibleItemCount="+mVisibleItemCount);
//	                loadBitmaps(mFirstVisibleItem, mVisibleItemCount);
//	            } 
//	        }
//	    }
//	}
//
//
//	    private class ScrollListenerImpl implements OnScrollListener{
//	        /**
//	         * 通过onScrollStateChanged获知:每次GridView停止滑动时加载图片
//	         * 但是存在一个特殊情况:
//	         * 当第一次入应用的时候,此时并没有滑动屏幕的操作即不会调用onScrollStateChanged,但应该加载图片.
//	         * 所以在此处做一个特殊的处理.
//	         * 即代码:
//	         * if (isFirstEnterThisActivity && visibleItemCount > 0) {
//	         *      loadBitmaps(firstVisibleItem, visibleItemCount);
//	         *      isFirstEnterThisActivity = false;
//	         *    }
//	         *    
//	         * ------------------------------------------------------------
//	         * 
//	         * 其余的都是正常情况.
//	         * 所以我们需要不断保存:firstVisibleItem和visibleItemCount
//	         * 从而便于中在onScrollStateChanged()判断当停止滑动时加载图片
//	         * 
//	         */
//	        @Override
//	        public void onScroll(AbsListView view, int firstVisibleItem,int visibleItemCount, int totalItemCount) {
//	            mFirstVisibleItem = firstVisibleItem;
//	            mVisibleItemCount = visibleItemCount;
//	            if (isFirstEnterThisActivity && visibleItemCount > 0) {
//	                System.out.println("---> 第一次进入该界面");
//	                loadBitmaps(firstVisibleItem, visibleItemCount);
//	                isFirstEnterThisActivity = false;
//	            }
//	        }
//	         
//	        /**
//	         *  GridView停止滑动时下载图片
//	         *  其余情况下取消所有正在下载或者等待下载的任务
//	         */
//	        @Override
//	        public void onScrollStateChanged(AbsListView view, int scrollState) {
//	            if (scrollState == SCROLL_STATE_IDLE) {
//	                System.out.println("---> GridView停止滑动  mFirstVisibleItem="+mFirstVisibleItem+",mVisibleItemCount="+mVisibleItemCount);
//	                loadBitmaps(mFirstVisibleItem, mVisibleItemCount);
//	            } 
//	        }
//	    }
//	}
//
//}
