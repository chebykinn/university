mod fold_f32;
mod fold;

use std::io::prelude::*;
use std::fs::File;
use std::fmt;

#[derive(Debug)]
struct Data {
    perfect: Vec<i32>,
    floating: Vec<i32>,
    cut: Vec<i32>,
    round: Vec<i32>,
    inc: Vec<i32>,
    round_input_2: Vec<i32>,
    round_input_4: Vec<i32>,
    round_weigh_2: Vec<i32>,
}

fn get_data(imp: &Vec<i32>, w: &Vec<i32>) -> Data {
    let x: Vec<f32> = fold_f32::fold_f32(
        &imp.iter().map(|&x| x as f32).collect(),
        &w.iter().map(|&x| x as f32 / 5.0).collect());
    Data {
        perfect: fold::fold(imp, 255, w, 255, 255, fold::RoundType::Round),
        floating: x.iter().map(|&x| (x * 5.0) as i32).collect(),
        cut: fold::fold(imp, 8, w, 4, 12, fold::RoundType::Cut),
        round: fold::fold(imp, 8, w, 4, 12, fold::RoundType::Round),
        inc: fold::fold(imp, 8, w, 4, 12, fold::RoundType::Inc),
        round_input_2: fold::fold(imp, 2, w, 4, 12, fold::RoundType::Round),
        round_input_4: fold::fold(imp, 4, w, 4, 12, fold::RoundType::Round),
        round_weigh_2: fold::fold(imp, 8, w, 2, 12, fold::RoundType::Round),
    }
}

fn sqr(i: i32) -> i64 {
    i as i64 * i as i64
}

fn stdev(std: &Vec<i32>, cmp: &Vec<i32>) -> f64 {
    let div: i64 = (0..std.len()).map(|x| sqr(std[x] - cmp[x])).sum();
    (div as f64 / std.len() as f64).sqrt()
}

struct Results(Vec<(usize, Data)>);

impl fmt::Display for Results {
    fn fmt(&self, f: &mut fmt::Formatter) -> fmt::Result {
        let r = |k : &Fn(&Data) -> &Vec<i32>, f: &mut fmt::Formatter| ->
            Vec<fmt::Result> {
                self.0.iter().map(|&(i, ref d)| write!(
                        f, "{} {}\n", i, stdev(&d.perfect, k(&d)))).collect()
            };

        try!(write!(f, "\"floating\"\n"));
        r(&(|d| &d.floating), f);
        try!(write!(f, "\n\n"));

        try!(write!(f, "\"fixed: cut lower bits\"\n"));
        r(&(|d| &d.cut), f);
        try!(write!(f, "\n\n"));

        try!(write!(f, "\"fixed: Increment higher bits\"\n"));
        r(&(|d| &d.inc), f);
        try!(write!(f, "\n\n"));

        try!(write!(f, "\"fixed: round higher bits\"\n"));
        r(&(|d| &d.round), f);
        try!(write!(f, "\n\n"));

        try!(write!(f, "\"4-bit input\"\n"));
        r(&(|d| &d.round_input_4), f);
        try!(write!(f, "\n\n"));

        try!(write!(f, "\"2-bit input\"\n"));
        r(&(|d| &d.round_input_2), f);
        try!(write!(f, "\n\n"));

        try!(write!(f, "\"2-bit weights\"\n"));
        r(&(|d| &d.round_weigh_2), f);
        write!(f, "\n")
    }
}

fn main() {
    for w in vec![
        ("u", vec![-1, -1, 4, -1, -1]),
        ("f", vec![8,  8,  8,  8,  8])].iter() {
            let mut z = Vec::new();
            for i in 10..41 {
                let len = i / (4 + (i % 4));
                let v : Vec<i32> = (0..i).map(|j| {
                    if j < (i - len) / 2 || j > (i + len) / 2
                    { 0 } else { 255 } }).collect();
                z.push((i, get_data(&v, &w.1)));
            }
            let mut f = File::create(format!("stdev_{}.dat", w.0)).unwrap();
            write!(f, "{}", Results(z));
        }
}

