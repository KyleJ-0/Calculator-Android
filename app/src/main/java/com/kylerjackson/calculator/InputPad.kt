package com.kylerjackson.calculator

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import java.util.ArrayList
import kotlin.math.sqrt
import android.widget.Button
import android.content.Context
import java.lang.ClassCastException
import kotlinx.android.synthetic.main.fragment_input_pad.*
import android.os.PersistableBundle

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class InputPad : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    val operatorPrecedence = HashMap<String, Int>()

    var operands : ArrayList<Double> = ArrayList()
    var operators : ArrayList<String> = ArrayList()
    var entryValue : String = ""
    var inputValue : Double? = null

    var result : Double = 0.0
    var displayText : String = ""
    var canAddOperator : Boolean = false

    var activityCallback: InputPad.InputPadListener?=null

    interface InputPadListener{
        fun onButtonClick(text:String)
    }

    override fun onAttach(context:Context){
        super.onAttach(context)
        try{
            activityCallback = context as InputPadListener
        }catch(e:ClassCastException){
            throw ClassCastException(context.toString()+" must implement InputPadListener")
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?){
        super.onActivityCreated(savedInstanceState)

        operatorPrecedence["%"] = 0
        operatorPrecedence["sqrt"] = 0
        operatorPrecedence["/"] = 2
        operatorPrecedence["x"] = 2
        operatorPrecedence["-"] = 1
        operatorPrecedence["+"] = 1
        operatorPrecedence["="] = 0
        operatorPrecedence["sign"] = 0
        operatorPrecedence["%"] = 2

        button_dec.setOnClickListener{v: View -> numberPressed(v,-1)}
        button_0.setOnClickListener{v: View -> numberPressed(v,0)}
        button_1.setOnClickListener{v: View -> numberPressed(v,1)}
        button_2.setOnClickListener{v: View -> numberPressed(v,2)}
        button_3.setOnClickListener{v: View -> numberPressed(v,3)}
        button_4.setOnClickListener{v: View -> numberPressed(v,4)}
        button_5.setOnClickListener{v: View -> numberPressed(v,5)}
        button_6.setOnClickListener{v: View -> numberPressed(v,6)}
        button_7.setOnClickListener{v: View -> numberPressed(v,7)}
        button_8.setOnClickListener{v: View -> numberPressed(v,8)}
        button_9.setOnClickListener{v: View -> numberPressed(v,9)}


        button_div.setOnClickListener{v: View -> operatorPressed(v,"/")}
        button_mult.setOnClickListener{v: View -> operatorPressed(v,"x")}
        button_sub.setOnClickListener{v: View -> operatorPressed(v,"-")}
        button_add.setOnClickListener{v: View -> operatorPressed(v,"+")}
        button_mod.setOnClickListener{v: View -> operatorPressed(v,"%")}


        button_sqrt.setOnClickListener{v: View -> specialButtons(v,"sqrt")}
        button_sign.setOnClickListener{v: View -> specialButtons(v,"sign")}
        button_equal.setOnClickListener{v: View -> specialButtons(v,"=")}
        button_ce.setOnClickListener{v: View -> specialButtons(v,"ce")}
        button_c.setOnClickListener{v: View -> specialButtons(v,"c")}
    }

    private fun numberPressed(view: View, value: Int){
        canAddOperator = true

        if(value == -1){
            if(!entryValue.contains(".",false)) {
                entryValue = entryValue + "."
            }
        }else {
            entryValue = entryValue + value
        }

        inputValue = entryValue.toDouble()

        displayText = entryValue
        activityCallback?.onButtonClick(displayText)

    }

    private fun specialButtons(view: View, button: String){
        if(button == "sign"){
            if(inputValue == null){
                if(operands.lastIndex == -1){ return }
                val lastEnteredNumber = operands.get(operands.lastIndex)
                operands.set(operands.lastIndex,lastEnteredNumber*-1)
                activityCallback?.onButtonClick(operands.get(operands.lastIndex).toString())
            }else{
                inputValue = inputValue!!*-1
                activityCallback?.onButtonClick(inputValue.toString())
            }

        }else if(button == "="){
            entryValue = ""
            if(inputValue != null) {
                operands.add(inputValue!!)
                calculate()
            }

        }else if(button == "sqrt"){
            if(inputValue == null){

                val lastEnteredNumber = operands.get(operands.lastIndex)
                result = sqrt(lastEnteredNumber)
                operands.set(operands.lastIndex,result)

                displayText = result.toString()
                activityCallback?.onButtonClick(displayText)

            }else{
                result = sqrt(inputValue!!)
                displayText = result.toString()
                activityCallback?.onButtonClick(displayText)

            }
        }else if(button == "c"){
            entryValue = ""
            inputValue = null
            displayText = "0"
            canAddOperator = false
            activityCallback?.onButtonClick(displayText)
            operands.clear()
            operators.clear()

        }else if(button == "ce"){
            if(!entryValue.isEmpty()){
                entryValue = entryValue.take(entryValue.length-1)

                inputValue = if(entryValue.length == 0){0.0}else{entryValue.toDouble()}
                displayText = inputValue.toString()

                activityCallback?.onButtonClick(displayText)
            }

        }
    }

    private fun operatorPressed(view: View, operator: String){
        if(!canAddOperator){ return }
        if(inputValue != null){operands.add(inputValue!!)}

        entryValue = ""
        inputValue = null
        canAddOperator = false


        var operatorsLength = operators.size
        var currentPrecedence : Int? = operatorPrecedence.get(operator)
        var prevPrecedence : Int? = if(operatorsLength>0){ operatorPrecedence.get( operators.get(operatorsLength-1)) }else{null}

        if(prevPrecedence!=null && currentPrecedence != null){

            if(currentPrecedence>prevPrecedence){
                operators.add(operator)
            }else if(currentPrecedence < prevPrecedence){
                calculate()
                operators.add(operator)
            }else if(currentPrecedence == prevPrecedence){
                calculate()
                operators.add(operator)
            }else{
                //remove ??
            }

        }else{
            operators.add(operator)
        }


    }

    private fun calculate(){

        for (i in (operators.size-1) downTo 0) {
            val op = operators.get(i)
            when(op){
                "+" -> result = operands.get(operands.size-2)+operands.get(operands.size-1)
                "-" -> result = operands.get(operands.size-2)-operands.get(operands.size-1)
                "x" -> result = operands.get(operands.size-2)*operands.get(operands.size-1)
                "/" -> result = operands.get(operands.size-2)/operands.get(operands.size-1)
                "%" -> result = operands.get(operands.size-2)%operands.get(operands.size-1)
            }
            operators.removeLast()

            operands.removeLast()
            operands.removeLast()
            operands.add(result)
        }
        inputValue = null
        displayText = result.toString()
        activityCallback?.onButtonClick(displayText)

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_input_pad, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment InputPad.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            InputPad().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}