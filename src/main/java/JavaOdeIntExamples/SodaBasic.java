/*
 * https://opensource.org/licenses/BSD-3-Clause
 *
 * Copyright (c) 2016, JodeInt developers
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list
 *    of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its contributors may be used to endorse or promote products derived
 *    from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
 * BUT NOT LIMITED TO,THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL
 * THE COPYRIGHT HOLDER OR CONTRIBUTORS BELIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED
 * AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF
 * THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package JavaOdeIntExamples;/**
 * Created by fons on 9/12/16.
 */
import com.kabouterlabs.jodeint.codepack.CodepackLibrary;
import org.bridj.Pointer;

class ExecFunc {
    OdeFunc function;
    double[] params;
    CodepackLibrary.codepack_ode_func f;
    Pointer<CodepackLibrary.codepack_ode_func> fptr;
    ExecFunc(OdeFunc fode) {

        function = fode;
        f = new CodepackLibrary.codepack_ode_func() {

            @Override
            public void apply(Pointer<Integer> neq, Pointer<Double> t_, Pointer<Double> q, Pointer<Double> qdot) {
                double[] qdot_ = qdot.getDoubles(neq.getInt());
                double[] q_    = q.getDoubles(neq.getInt());
                function.apply(neq.getInt(), t_.get(),q_, qdot_, params);
                qdot.setDoubles(qdot_);
                q.setDoubles(q_);

            }
        };
        fptr =  org.bridj.Pointer.getPointer(f);
    }

    Pointer<CodepackLibrary.codepack_ode_func> f_func () {return fptr;}
};

public class SodaBasic {


    private Pointer<Double> qq;
    private int dimension;
    private ExecFunc ff;

    public  SodaBasic(int d, OdeFunc func){
        dimension = d;
        ff = new ExecFunc(func);
        double[] initial_conditions = new double[d];
        qq = Pointer.pointerToDoubles(initial_conditions);
    }

    public void exec(String fn, double[] init, double start, double end, double delta) {

        int index = 0;
        for (Double value : init) {
            qq.set(index, value);
            index++;
        }
        Pointer<Double> stack = PrintStack.create(start,end,delta, dimension);
        CodepackLibrary.lsoda_basic(stack, qq,ff.f_func(),dimension,start, end, delta);
        PrintStack.print(stack, start,end,delta,dimension,fn);
    }


    public void exec(String fn, double[] params, double[] init, double start, double end, double delta) {
        ff.params = params;
        exec(fn,init,start,end,delta);
    }

}



