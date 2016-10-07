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
 * Created by fons on 10/2/16.
 */

import com.kabouterlabs.jodeint.codepack.CodepackLibrary;
import org.bridj.Pointer;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

class SodarExecFunc {

    OdeFunc function;
    double[] params;
    CodepackLibrary.dlsodar_f_callback f;
    Pointer<CodepackLibrary.dlsodar_f_callback> fptr;
    public SodarExecFunc(OdeFunc fode) {

        function = fode;
        f = new CodepackLibrary.dlsodar_f_callback() {

            @Override
            public void apply(Pointer<Integer> neq, Pointer<Double> t_, Pointer<Double> q, Pointer<Double> qdot) {
                double[] qdot_ = qdot.getDoubles(neq.getInt());
                double[] q_    = q.getDoubles(neq.getInt());
                function.apply(neq.getInt(), t_.get(),q_, qdot_, params);
                qdot.setDoubles(qdot_);
                q.setDoubles(q_);

            }
        };
        fptr =   org.bridj.Pointer.getPointer(f);
    }

    Pointer<CodepackLibrary.dlsodar_f_callback> f_func () {return fptr;}
};

class SodarConstraintFunc {
    ConstraintFunc function;
    private CodepackLibrary.dlsodar_g_callback g;
    private Pointer<CodepackLibrary.dlsodar_g_callback> gptr;
    double[] params;
    public SodarConstraintFunc(ConstraintFunc cf) {

        function = cf;
        g = new CodepackLibrary.dlsodar_g_callback() {
            @Override
            public void apply(Pointer<Integer> neq, Pointer<Double> t_, Pointer<Double> y, Pointer<Integer> ng, Pointer<Double> gout) {
                double[] gout_ = gout.getDoubles(ng.get());
                double[] y_    = y.getDoubles(neq.get());
                function.apply(neq.getInt(), t_.get(),y_,ng.getInt(),gout_);
                gout.setDoubles(gout_);
                y.setDoubles(y_);
            }
        };
        gptr =  Pointer.getPointer(g);
    }

    Pointer<CodepackLibrary.dlsodar_g_callback> g_func () {return gptr;}

};


class SodarEventFunc {
    EventFunc function;
    double[] params;
    public SodarEventFunc(EventFunc f){function = f;}
    public void apply(Pointer<Integer> neq, Pointer<Double> t, Pointer<Double> y, Pointer<Integer> ng, Pointer<Integer> jroot) {
        double[] y_ = y.getDoubles(neq.get());
        function.apply(neq.get(), t.get(), y_, ng.get(), jroot.getInts(ng.get()), params);
        y.setDoubles(y_);
    }
}


public class Sodar {
    private SodarExecFunc ff;
    private SodarConstraintFunc gg;
    private SodarEventFunc ee;
    private Pointer<Integer> neq;
    private Pointer<Integer> ng;
    private Pointer<Integer> itol = Pointer.pointerToInt((int) CodepackLibrary.codepack_itol_e.ALL_SCALAR.value);
    private Pointer<Double>  atol = Pointer.pointerToDouble(10e-12);
    private Pointer<Double>  rtol = Pointer.pointerToDouble(10e-12);
    private Pointer<Integer> itask = Pointer.pointerToInt((int)CodepackLibrary.codepack_itask_e.NORMAL.value);
    private Pointer<Integer>  istate = Pointer.pointerToInt((int)CodepackLibrary.codepack_istate_in_e.FIRST_CALL.value);
    private Pointer<Integer>  iopt   = Pointer.pointerToInt((int)CodepackLibrary.codepack_iopt_e.NO_OPTIONAL_INPUTS.value);
    private Pointer<Integer>  jt     = Pointer.pointerToInt((int)CodepackLibrary.codepack_jac_type_e.INTERNAL.value());
    private Pointer<Integer> lrw;
    private Pointer<Double>  rwork;
    private Pointer<Integer> iwork;
    private Pointer<Integer> liw;
    private Pointer<Integer> jroot;
    private Pointer<Double>  qq;
    private double[]         gout;

    public Sodar(OdeFunc o_func, ConstraintFunc c_func, EventFunc e_func) {
        this(o_func,c_func);
        ee = new SodarEventFunc(e_func);
    }
    public Sodar(OdeFunc o_func, ConstraintFunc c_func) {
        this(o_func);
        ng  = Pointer.pointerToInt(c_func.dim());
        gg = new SodarConstraintFunc(c_func);


        int lrn = 20 + 16 * neq.getInt() + 3 * ng.getInt();
        int lrs = 22 + 9 * neq.getInt() + neq.getInt()*neq.getInt() +  3 * ng.getInt();
        if (lrn > lrs) {
            lrw = Pointer.pointerToInt(lrn);
        }
        else {
            lrw = Pointer.pointerToInt(lrs);
        }
        rwork = Pointer.allocateDoubles(lrw.get());
        liw   = Pointer.pointerToInt(neq.get() + 20);
        iwork = Pointer.allocateInts(liw.get());
        jroot = Pointer.allocateInts(ng.get());
        gout  = new double[ng.get()];
        qq = Pointer.allocateDoubles(o_func.dim());
    }
    public Sodar(OdeFunc o_func)
    {
        ff = new SodarExecFunc(o_func);
        gg = new SodarConstraintFunc(new ConstraintFunc(0) {
                @Override
                public void apply(int dim, double t, double[] y, int ng, double[] groot) {
                }
            }
        );
        ee = null;
        neq = Pointer.pointerToInt(o_func.dim());
        ng  = Pointer.pointerToInt(0);
        int lrn = 20 + 16 * neq.getInt() + 3 * ng.getInt();
        int lrs = 22 + 9 * neq.getInt() + neq.getInt()*neq.getInt() +  3 * ng.getInt();
        if (lrn > lrs) {
            lrw = Pointer.pointerToInt(lrn);
        }
        else {
            lrw = Pointer.pointerToInt(lrs);
        }
        rwork = Pointer.allocateDoubles(lrw.get());
        liw   = Pointer.pointerToInt(neq.get() + 20);
        iwork = Pointer.allocateInts(liw.get());
        jroot = Pointer.allocateInts(ng.get());
        gout  = new double[ng.get()];
        qq = Pointer.allocateDoubles(o_func.dim());
    }
    public void exec(String fn, String zn,double[] params,double[] init, double start, double end, double delta) {
        ff.params = params;
        exec(fn,zn,init,start,end,delta);
    }

    public void exec(String fn, double[] params,double[] init, double start, double end, double delta) {
        ff.params = params;
        exec(fn,"tmp",init,start,end,delta);
    }

    public void exec(String fn, String zn, double[] init, double start, double end, double delta) {

        int index = 0;
        for (Double value : init) {
            qq.set(index, value);
            index++;
        }
        Pointer<Double> tp       = Pointer.pointerToDouble(start);
        try
        {
            FileWriter fstream = new FileWriter( PrintStack.path(fn), false); //true tells to append data.
            BufferedWriter out = new BufferedWriter(fstream);
            FileWriter fstreamz = new FileWriter( PrintStack.path(zn), false); //true tells to append data.
            BufferedWriter outz = new BufferedWriter(fstreamz);
            outz.write("~~roots found\n");
            outz.write("index, root value, t,");
            for (int j = 0; j < neq.get();j++) {
                outz.write("y" + ((Integer)j).toString()+",");
            }
            outz.write("\n");
            while ( tp.get() < end) {
                Pointer<Double> tnextp = Pointer.pointerToDouble(tp.get()+delta);
                CodepackLibrary.dlsodar(ff.f_func(),neq,qq,tp,tnextp,itol,rtol,atol,itask,istate,iopt,rwork,lrw,iwork,liw,null,jt,gg.g_func(),ng,jroot);
                out.write(tp.get().toString() + ",");
                index = 0;
                while (index < neq.get())
                {
                    out.write(qq.get(index).toString() + ",");
                    index++;
                }
                out.write("\n");
                if (istate.get() == CodepackLibrary.codepack_istate_out_e.ROOT_FOUND.value) {
                    gg.function.apply(neq.get(),tp.get(),qq.getDoubles(neq.get()),ng.get(),gout);
                    for (index = 0; index < ng.get(); index++){
                         if (jroot.get(index) == 1) {
                             outz.write(((Integer)index).toString() + "," + ((Double)gout[index]).toString() + ","+tp.get().toString() + ",");
                             for (int j = 0; j < neq.get();j++) {
                                outz.write( qq.get(j).toString()+",");
                             }
                             outz.write("\n");
                         }
                    }
                    if (ee != null) {
                        ee.apply(neq, tp, qq, ng, jroot);
                        istate = Pointer.pointerToInt((int)CodepackLibrary.codepack_istate_in_e.FIRST_CALL.value);
                    }
                }
            }
            out.close();
            outz.close();

        }
        catch (IOException e)
        {
            System.err.println("Error: " + e.getMessage());
        }

    }
}

