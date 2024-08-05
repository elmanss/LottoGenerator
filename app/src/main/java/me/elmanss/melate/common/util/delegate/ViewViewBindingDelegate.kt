package me.elmanss.melate.common.util.delegate

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

/**
 * Create bindings for a view similar to bindView.
 *
 * To use, just call: private val binding: FHomeWorkoutDetailsBinding by viewBinding() with your
 * binding class and access it as you normally would.
 */
inline fun <reified T : ViewBinding> ViewGroup.viewBinding() =
  ViewBindingDelegate(T::class.java, this)

class ViewBindingDelegate<T : ViewBinding>(
  private val bindingClass: Class<T>,
  private val view: ViewGroup,
) : ReadOnlyProperty<ViewGroup, T> {
  private val binding: T =
    try {
      val inflateMethod =
        bindingClass.getMethod(
          "inflate",
          LayoutInflater::class.java,
          ViewGroup::class.java,
          Boolean::class.javaPrimitiveType,
        )
      inflateMethod.invoke(null, LayoutInflater.from(view.context), view, true).cast<T>()
    } catch (e: NoSuchMethodException) {
      // <merge> tags don't have the boolean parameter.
      val inflateMethod =
        bindingClass.getMethod("inflate", LayoutInflater::class.java, ViewGroup::class.java)
      inflateMethod.invoke(null, LayoutInflater.from(view.context), view).cast<T>()
    }

  override fun getValue(thisRef: ViewGroup, property: KProperty<*>): T = binding
}

@Suppress("UNCHECKED_CAST") private fun <T> Any.cast(): T = this as T
