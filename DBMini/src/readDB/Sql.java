package readDB;

import java.util.HashMap;
import java.util.Map;

public class Sql {
	String[] keys = {
			"그룹 함수 조회 예제",
			"조인 조회 예제 1",
			"조인 조회 예제 2",
			"서브쿼리 조회 예제"
	};
	String[] sqls = {
			"SELECT JOB, AVG(SAL) FROM EMP GROUP BY JOB HAVING AVG(SAL) >= 1000",
			"select d.deptno,d.dname,e.empno,e.ename,e.job,e.sal from emp e,dept d where e.deptno(+) = d.deptno order by d.deptno,e.empno",
			"select d.deptno,d.dname,e.empno,e.ename,e.mgr,e.sal,e.deptno as deptno_1,s.losal,s.hisal,s.grade,e2.empno mgr_empno,e2.ename mgr_ename from emp e right outer join dept d on (e.deptno = d.deptno) left outer join salgrade s on (e.sal between s.losal and s.hisal) left outer join emp e2 on (e.mgr = e2.empno) order by deptno, empno",
			"select * from emp where sal > any (select min(sal) from emp where job = 'SALESMAN')"
	};
	Map<String, String> hashMap = new HashMap<String, String>();
	public Map<String, String> sql(){
		for(int i = 0 ; i<keys.length ; i++) {
		hashMap.put(keys[i], sqls[i]);
		}
		return hashMap;
	}
}
