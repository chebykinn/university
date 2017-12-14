use std::iter::FromIterator;

pub fn fold_f32<B: FromIterator<f32>> (a: &Vec<f32>, b: &Vec<f32>) -> B {
    (0..a.len() + b.len() - 1).map(
        |k| (0 .. k + 1).map(
            |i| *a.get(i).unwrap_or(&0.0) * *b.get(k - i).unwrap_or(&0.0))
        .sum()).collect()
}

#[cfg(test)]
mod tests {
    #[test]
    fn test_fold_32() {
        let a = vec![2.0, 1.0, 3.0, -1.0];
        let b = vec![-1.0, 1.0, 2.0];
        let c = vec![-2.0, 1.0, 2.0, 6.0, 5.0, -2.0];
        let d : Vec<f32> = super::fold_f32(&a, &b);
        assert_eq!(c, d);
    }
}

