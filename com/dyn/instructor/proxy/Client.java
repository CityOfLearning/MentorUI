package com.dyn.instructor.proxy;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;

public class Client implements Proxy {

        /**
         * @see forge.reference.proxy.Proxy#renderGUI()
         */
        @Override
        public void renderGUI() {
                // Render GUI when on call from client
        } 
}