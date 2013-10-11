package com.agileapes.dragonfly.assets;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/10/11, 12:20)
 */
interface PostProcessor<C, P> {

    void postProcess(C context, P postProcessor);

}
