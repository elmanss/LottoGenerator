package me.elmanss.melate.common.util.delegate

import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.viewbinding.ViewBinding
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

/**
 * Create bindings for a view similar to bindView.
 *
 * To use, just call private val binding: FHomeWorkoutDetailsBinding by viewBinding() with your
 * binding class and access it as you normally would.
 */
inline fun <reified T : ViewBinding> Fragment.viewBinding() =
  FragmentViewBindingDelegate(T::class.java, this)

class FragmentViewBindingDelegate<T : ViewBinding>(
  private val bindingClass: Class<T>,
  val fragment: Fragment,
) : ReadOnlyProperty<Fragment, T> {
  private val clearBindingHandler by
    lazy(LazyThreadSafetyMode.NONE) { Handler(Looper.getMainLooper()) }
  private var binding: T? = null

  private val bindMethod = bindingClass.getMethod("bind", View::class.java)

  init {
    fragment.viewLifecycleOwnerLiveData.observe(fragment) { viewLifecycleOwner ->
      viewLifecycleOwner.lifecycle.addObserver(
        object : LifecycleObserver {
          @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
          fun onDestroy() {
            // Lifecycle listeners are called before onDestroyView in a Fragment.
            // However, we want views to be able to use bindings in onDestroyView
            // to do cleanup so we clear the reference one frame later.
            clearBindingHandler.post { binding = null }
          }
        }
      )
    }
  }

  override fun getValue(thisRef: Fragment, property: KProperty<*>): T {
    // onCreateView may be called between onDestroyView and next Main thread cycle.
    // In this case [binding] refers to the previous fragment view. Check that binding's root view
    // matches current fragment view
    if (binding != null && binding?.root !== thisRef.view) {
      binding = null
    }
    binding?.let {
      return it
    }

    val lifecycle = fragment.viewLifecycleOwner.lifecycle
    if (!lifecycle.currentState.isAtLeast(Lifecycle.State.INITIALIZED)) {
      error("Cannot access view bindings. View lifecycle is ${lifecycle.currentState}!")
    }

    binding = bindMethod.invoke(null, thisRef.requireView()).cast<T>()
    return binding!!
  }
}

@Suppress("UNCHECKED_CAST") private fun <T> Any.cast(): T = this as T
