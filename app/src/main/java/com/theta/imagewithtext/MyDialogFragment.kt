package com.theta.imagewithtext


import android.app.DialogFragment
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_my_dialog.view.*


/**
 * A simple [Fragment] subclass.
 */
class MyDialogFragment : DialogFragment() {

    var editDialogListener: EditDialogListener? = null

    companion object {
        fun newInstance(): MyDialogFragment {
            return MyDialogFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view: View = inflater!!.inflate(R.layout.fragment_my_dialog, container, false)

        view.btnCnt.setOnClickListener(View.OnClickListener {
            if (view.etText.toString().isEmpty()){
                view.etText.setError(getString(R.string.requiredFieldError))
                view.etText.requestFocus()
            }else{
                editDialogListener = activity as EditDialogListener
                editDialogListener!!.updateResult(view.etText.getText().toString())
                dismiss()
            }


        })

        return view;
    }


    interface EditDialogListener {
        fun updateResult(inputText: String)
    }

}// Required empty public constructor
