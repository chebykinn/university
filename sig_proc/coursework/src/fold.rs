use std::iter::FromIterator;

fn get_sign(a: i32) -> i32 {
    if a > 0 { 1 } else if a < 0 { -1 } else { 0 }
}

#[derive(Copy, Clone)]
pub enum RoundType {
    Cut,
    Inc,
    Round,
}

fn round(a: &Vec<i32>, size: usize, rtype: RoundType) -> Vec<i32> {
    let s = if a.iter().any(|&x| x < 0) { size - 1 } else { size };

    if let Some(Some(n)) = a.iter().map(
        |&x| (0..31).rev().find(|i| x.abs() & (1 << i) != 0))
        .filter(|x| x.is_some()).max() {
        if n >= s {
            a.iter().map(|&x| {
                let t = x.abs();
                let mo = n + 1 - s;
                get_sign(x) * {
                    match rtype {
                        RoundType::Cut =>
                            t & (!(1i32 << s) << mo),
                        RoundType::Inc =>
                            (1 + (t >> mo)) << mo,
                        RoundType::Round =>
                            if t & (1 << (n - s)) != 0 {
                                (1 + (t >> mo)) << mo
                            } else {
                                t & (!(1i32 << s) << mo)
                            },
                    }
                }
            }).collect()
        } else {
            a.clone()
        }
    } else {
        a.clone()
    }
}

pub fn fold<C: FromIterator<i32>> (a: &Vec<i32>, asize: usize,
                                   b: &Vec<i32>, bsize: usize,
                                   csize: usize, rtype: RoundType) -> C {
    let a2 = round(a, asize, rtype);
    let b2 = round(b, bsize, rtype);
    (0..a.len() + b.len() - 1).map(
        |k| (0..k + 1).map(
            |i| round(&vec![
                      *a2.get(i).unwrap_or(&0) * *b2.get(k - i).unwrap_or(&0)
            ], csize, rtype)[0])
        .fold(0, |a, c| round(&vec![a + c], csize, rtype)[0])).collect()
}

#[cfg(test)]
mod tests {
    #[test]
    fn test_fold() {
        let a = vec![2, 1, 3, -1];
        let b = vec![-1, 1, 2];
        let c = vec![-2, 1, 2, 6, 5, -2];
        let d : Vec<i32> = super::fold(&a, 8, &b, 8, 8, super::RoundType::Cut);
        assert_eq!(c, d);
    }

    #[test]
    fn test_round() {
        use super::round;
        use super::RoundType;

        assert_eq!(round(&vec![14], 8, RoundType::Cut),   vec![14]);
        assert_eq!(round(&vec![14], 2, RoundType::Cut),   vec![12]);
        assert_eq!(round(&vec![14], 1, RoundType::Cut),   vec![8 ]);
        assert_eq!(round(&vec![14], 8, RoundType::Inc),   vec![14]);
        assert_eq!(round(&vec![14], 2, RoundType::Inc),   vec![16]);
        assert_eq!(round(&vec![14], 1, RoundType::Inc),   vec![16]);
        assert_eq!(round(&vec![14], 8, RoundType::Round), vec![14]);
        assert_eq!(round(&vec![14], 2, RoundType::Round), vec![16]);
        assert_eq!(round(&vec![14], 1, RoundType::Round), vec![16]);
        assert_eq!(round(&vec![31], 8, RoundType::Round), vec![31]);
        assert_eq!(round(&vec![32], 1, RoundType::Round), vec![32]);

        assert_eq!(round(&vec![-1, -1, 4, -1, -1], 2, RoundType::Round),
            vec![0, 0, 4, 0, 0]);
    }
}

