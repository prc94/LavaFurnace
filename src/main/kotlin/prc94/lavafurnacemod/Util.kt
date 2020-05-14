package prc94.lavafurnacemod

import net.minecraft.util.IIntArray
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

fun delegateIntArrayEntry(array: IIntArray, index: Int) = object : ReadWriteProperty<Any?, Int> {
    override fun getValue(thisRef: Any?, property: KProperty<*>): Int = array[index]

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: Int) {
        array[index] = value
    }
}