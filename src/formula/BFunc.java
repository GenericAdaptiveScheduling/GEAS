/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package formula;

import java.security.KeyStore.Entry;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import middleware.SameContextChange;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import context.Context;
import context.ContextChange;
import context.Element;

/**
 *
 * @author why
 * terminal bfunc implementations
 */
public class BFunc extends Formula {
	//bfunc parameters
    private static class Param {

        public String var = null, field = null;

        public Param(String _var, String _field) {

            var = _var;
            field = _field;
        }
    }
    //saving paras
    private HashMap<String,Param> params = new HashMap<String,Param>();
    
    @SuppressWarnings("unused")
	private  static Log logger = LogFactory.getLog(BFunc.class.getName());
    
    public BFunc(String name) {
        super(name);
    }
    
    public HashMap<String,Param> getParam() {
    	return params;
    }
    
    public void setParam(HashMap<String,Param> params) {
    	this.params = params;
    }
    
    public void setParam(String pos, String var, String field) {
        if (params.get(pos) == null) {
            params.put(pos, new Param(var, field));
        } else {  // pos should be unique
            System.out.println("incorrect position");
            System.exit(1);
        }
    }
    
    private long convert(String time) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss:SSS" );
        Date date = sdf.parse(time);
        return date.getTime();
    }
    
    private String getValue(int pos, HashMap<String,Element> varEnv) {
        
    	/*for(Map.Entry entry: varEnv.entrySet()){
    		System.out.println(entry.getKey()+"--"+entry.getValue());
    	}*/
        Param p = params.get(Integer.toString(pos));
        if (p == null) {
            System.out.println("incorrect position: " + pos);
            System.exit(1);
        }
        //System.out.println(p.field);
        String v = varEnv.get(p.var).get(p.field);
        return v;
    }
    
    private boolean funcTest(HashMap<String, Element> varEnv) {
        String v1 = getValue(1, varEnv);
    	if (v1 == null) return false;
        
    	
    	int lon = Integer.parseInt(v1);  // Longitude
    	
    	// The longitude and latitude should be in [112, 116] and [20, 24], respectively
    	boolean result = true;
    	if (lon < 112 || lon > 116) {
    		result = false;
    	}
    	
    	return result;
    }
    
    private boolean funcTrue(HashMap<String, Element> varEnv) {

        return true;
    }
    
    // Compares a field value to a constant string (should be idential)
    private boolean funcEqual(HashMap<String, Element> varEnv) {

        String v1 = getValue(1, varEnv);
        if (v1 == null) {
            return false;
        }

        // The second value should be identical to the first value
        Param p2 = params.get(Integer.toString(2));  // pos = 2
        boolean result = true;
        if (p2 == null) {
            return false;
        } else {
            String v2 = p2.var;  // Should be a string constant
            if (!v1.equals(v2)) {
                result = false;
            }
        }

        return result;
    }

    // Compares a field value to a constant string (should not be idential)
    private boolean funcNotEqual(HashMap<String, Element> varEnv) {

        String v1 = getValue(1, varEnv);
        if (v1 == null) {
            return false;
        }

        // The second value should not be identical to the first value
        Param p2 = params.get(Integer.toString(2));  // pos = 2
        boolean result = true;
        if (p2 == null) {
            return false;
        } else {
            String v2 = p2.var;  // Should be a string constant
            if (v1.equals(v2)) {
                result = false;
            }
        }

        return result;
    }

    // Compares two field values and a constant string if any (should be idential)
    private boolean funcSame(HashMap<String,Element> varEnv) {
    	//System.out.println("varEnv: "+varEnv.toString());
        String v1 = getValue(1, varEnv);
        String v2 = getValue(2, varEnv);
        if (v1 == null || v2 == null) {
            return false;
        }

        // The first two values should be identical
        if (!v1.equals(v2)) {
            return false;
        }

        // The third value (if any) should be identical to the first two values
        Param p3 = params.get(Integer.toString(3));  // pos = 3
        if (p3 != null) {
            String v3 = p3.var;  // Should be a string constant
            if (!v1.equals(v3)) {
                return false;
            }
        }

        return true;
    }
    /*private boolean funcSameSymbolic(String v1, String v2) {
    	if(v1 == null || v2 == null)
    		return false;
    	else if(!v1.equals(v2))
    		return false;
    	else if()
    }
*/
    // Compares two field values (should not be idential)
    private boolean funcNotSame(HashMap<String, Element> varEnv) {

        String v1 = getValue(1, varEnv);
        String v2 = getValue(2, varEnv);
        if (v1 == null || v2 == null) {
            return false;
        }

        // The first two values should not be identical
        boolean result = true;
        if (v1.equals(v2)) {
            result = false;
        }

        return result;
    }

    private boolean funcSmaller(HashMap<String, Element> varEnv) {

        String v1 = getValue(1, varEnv);
        String v2 = getValue(2, varEnv);
        if (v1 == null || v2 == null) {
            return false;
        }

        // The number from v1 should be smaller than that from v2
        int n1 = (new Integer(v1)).intValue();
        int n2 = (new Integer(v2)).intValue();
        if (n1 >= n2) {
            return false;
        }

        return true;
    }

    private boolean funcOverlap(HashMap<String, Element> varEnv) {

        String v1 = getValue(1, varEnv);
        String v2 = getValue(2, varEnv);
        String v3 = getValue(3, varEnv);
        String v4 = getValue(4, varEnv);
        if (v1 == null || v2 == null || v3 == null || v4 == null) {
            return false;
        }

        // Two values should be overlapping
        if (v1.compareTo(v4) > 0 || v2.compareTo(v3) < 0) {
            return false;
        }

        return true;
    }

    private boolean funcBefore(HashMap<String, Element> varEnv) throws ParseException {

        String v1 = getValue(1, varEnv);
        String v2 = getValue(2, varEnv);
        Param p3 = params.get(Integer.toString(3));  // pos = 3
        if (p3 == null) {
            System.out.println("incorrect position");
            System.exit(1);
        }
        String v3 = p3.var;
        if (v1 == null || v2 == null || v3 == null) {
            return false;
        }

        long t1 = convert(v1);
        long t2 = convert(v2);
        long t = Long.parseLong(v3);

        // t1 <= t2 && t1 + t >= t2 should hold
        if (t1 > t2 || t1 + t < t2) {
            return false;
        }

        return true;
    }

    // For SUTPC
    
    private boolean funcSZLocRange(HashMap<String, Element> varEnv) {

    	String v1 = getValue(1, varEnv);
        //System.out.println(v1);
    	if (v1 == null) return false;
        
    	StringTokenizer st = new StringTokenizer(v1, "_");  // Format: longitude_latitude_speed
    	double lon = Double.parseDouble(st.nextToken());  // Longitude
    	double lat = Double.parseDouble(st.nextToken());  // Latitude
    	
    	// The longitude and latitude should be in [112, 116] and [20, 24], respectively
    	boolean result = true;
    	if (lon < 112.0 || lon > 116.0 || lat < 20.0 || lat > 24.0) {
    		result = false;
    	}
    	
    	return result;
    }
    
    // For SUTPC
    
    private boolean funcSZLocDist(HashMap<String, Element> varEnv) {

    	String v1 = getValue(1, varEnv);
    	String v2 = getValue(2, varEnv);
    	if (v1 == null || v2 == null) {
    		return false;
    	}

    	StringTokenizer st = new StringTokenizer(v1, "_");  // Format: longitude_latitude_speed
    	double lon1 = Double.parseDouble(st.nextToken());  // Longitude 1
    	double lat1 = Double.parseDouble(st.nextToken());  // Latitude 1
    	st = new StringTokenizer(v2, "_");  // Format: longitude_latitude_speed
    	double lon2 = Double.parseDouble(st.nextToken());  // Longitude 2
    	double lat2 = Double.parseDouble(st.nextToken());  // Latitude 2
    	double dist = Math.sqrt((lon2 - lon1) * (lon2 - lon1) + (lat2 - lat1) * (lat2 - lat1));
    	
    	// The distance should be no more than 0.026 (assuming the speed is no more than 200 km/h)
    	boolean result = true;
    	if (dist > 0.025) {
    		result = false;
    	}
    	
    	return result;
    }
    
    private boolean funcSZLocDistNeq(HashMap<String, Element> varEnv) {

    	String v1 = getValue(1, varEnv);
    	String v2 = getValue(2, varEnv);
    	if (v1 == null || v2 == null) {
    		return false;
    	}

    	StringTokenizer st = new StringTokenizer(v1, "_");  // Format: longitude_latitude_speed
    	double lon1 = Double.parseDouble(st.nextToken());  // Longitude 1
    	double lat1 = Double.parseDouble(st.nextToken());  // Latitude 1
    	st = new StringTokenizer(v2, "_");  // Format: longitude_latitude_speed
    	double lon2 = Double.parseDouble(st.nextToken());  // Longitude 2
    	double lat2 = Double.parseDouble(st.nextToken());  // Latitude 2
    	double dist = Math.sqrt((lon2 - lon1) * (lon2 - lon1) + (lat2 - lat1) * (lat2 - lat1));
    	
    	// The distance should be no more than 0.026 (assuming the speed is no more than 200 km/h)
    	boolean result = true;
    	if (dist > 0.025 || dist == 0) {
    		result = false;
    	}
    	
    	return result;
    }
    
    // For SUTPC
    
    private boolean funcSZLocClose(HashMap<String, Element> varEnv) {

    	String v1 = getValue(1, varEnv);
    	String v2 = getValue(2, varEnv);
    	if (v1 == null || v2 == null){//||v1.isEmpty()||v2.isEmpty()) {
    		return false;
    	}
    	
    	StringTokenizer st = new StringTokenizer(v1, "_");  // Format: longitude_latitude_speed
    	double lon1 = Double.parseDouble(st.nextToken());  // Longitude 1
    	double lat1 = Double.parseDouble(st.nextToken());  // Latitude 1
    	st = new StringTokenizer(v2, "_");  // Format: longitude_latitude_speed
    	double lon2 = Double.parseDouble(st.nextToken());  // Longitude 2
    	double lat2 = Double.parseDouble(st.nextToken());  // Latitude 2
    	double dist = Math.sqrt((lon2 - lon1) * (lon2 - lon1) + (lat2 - lat1) * (lat2 - lat1));
    	
    	// The distance should be no more than 0.001 as 'close'
    	boolean result = true;
    	if (dist > 0.001) {
    		result = false;
    	}

    	return result;
    }

    // For SUTPC
    
    private boolean funcSZSpdClose(HashMap<String, Element> varEnv) {

    	String v1 = getValue(1, varEnv);
    	String v2 = getValue(2, varEnv);
    	if (v1 == null || v2 == null) {
    		return false;
    	}

    	StringTokenizer st = new StringTokenizer(v1, "_");  // Format: longitude_latitude_speed
    	st.nextToken();  // Skip longitude
    	st.nextToken();  // Skip latitude
    	int spd1 = Integer.parseInt(st.nextToken());  // Speed 1
    	st = new StringTokenizer(v2, "_");  // Format: longitude_latitude_speed
    	st.nextToken();  // Skip longitude
    	st.nextToken();  // Skip latitude
    	int spd2 = Integer.parseInt(st.nextToken());  // Speed 2
    	
    	// The speed difference should be no more than 50 (as 'close')
    	boolean result = true;
    	if (Math.abs(spd2 - spd1) > 50) {
    		result = false;
    	}

    	return result;
    }

    // For Siafu

    private boolean funcSFLate(HashMap<String, Element> varEnv) {
    	
    	String v1 = getValue(1, varEnv);
    	if (v1 == null) {
    		return false;
    	}
    	
    	int hour = Integer.parseInt(v1.substring(11, 13));  // Hour in simulation time
    	boolean result = false;
    	if (hour >= 19) {  // Later than 7pm
    		result = true;
    	}
    	
    	return result;
    }

    // For Siafu
    
    private boolean funcSFWorktime(HashMap<String, Element> varEnv) {
    	
    	String v1 = getValue(1, varEnv);
    	if (v1 == null) {
    		return false;
    	}
    	
    	int hour = Integer.parseInt(v1.substring(11, 13));  // Hour in simulation time
    	boolean result = false;
    	if (hour >= 8 && hour <= 17) {  // Between 8am and 5pm
    		result = true;
    	}
    	
    	return result;
    }

    // For Siafu
    
    private boolean funcSFClose(HashMap<String, Element> varEnv) {

    	String v1 = getValue(1, varEnv);
    	String v2 = getValue(2, varEnv);
    	if (v1 == null || v2 == null) {
    		return false;
    	}

    	StringTokenizer st = new StringTokenizer(v1, "#");  // Format: latitude#longitude
    	double lat1 = Double.parseDouble(st.nextToken());  // Latitude 1
    	double lon1 = Double.parseDouble(st.nextToken());  // Longitude 1
    	st = new StringTokenizer(v2, "#");  // Format: latitude#longitude
    	double lat2 = Double.parseDouble(st.nextToken());  // Latitude 2
    	double lon2 = Double.parseDouble(st.nextToken());  // Longitude 2
    	double dist = Math.sqrt((lon2 - lon1) * (lon2 - lon1) + (lat2 - lat1) * (lat2 - lat1));

    	// The distance should be no more than 0.000012 (100 m) as 'close'
    	boolean result = true;
    	if (dist > 0.000012) {
    		result = false;
    	}

    	return result;
    }

    // For Siafu
    
    private boolean funcSFFar(HashMap<String, Element> varEnv) {

    	String v1 = getValue(1, varEnv);
    	String v2 = getValue(2, varEnv);
    	if (v1 == null || v2 == null) {
    		return false;
    	}

    	StringTokenizer st = new StringTokenizer(v1, "#");  // Format: latitude#longitude
    	double lat1 = Double.parseDouble(st.nextToken());  // Latitude 1
    	double lon1 = Double.parseDouble(st.nextToken());  // Longitude 1
    	st = new StringTokenizer(v2, "#");  // Format: latitude#longitude
    	double lat2 = Double.parseDouble(st.nextToken());  // Latitude 2
    	double lon2 = Double.parseDouble(st.nextToken());  // Longitude 2
    	double dist = Math.sqrt((lon2 - lon1) * (lon2 - lon1) + (lat2 - lat1) * (lat2 - lat1));

    	// The distance should be no less than 0.000018 (150 m) as 'far'
    	boolean result = true;
    	if (dist < 0.000018) {
    		result = false;
    	}

    	return result;
    }
    
    
    @Override
    public boolean evaluateEcc(HashMap<String,Context> contexts,RuntimeNode node) {
        boolean result = false;

        if (kind.equals("test")) {
            result = funcTest(node.getVar());
        } else if (kind.equals("true") || kind.equals("TRUE")) {
            result = funcTrue(node.getVar());
        } else if (kind.equals("equal") || kind.equals("Equal")) {
            result = funcEqual(node.getVar());
        } else if (kind.equals("not_equal")) {
            result = funcNotEqual(node.getVar());
        } else if (kind.equals("same") || kind.equals("Same")) {
            result = funcSame(node.getVar());
        } else if (kind.equals("not_same")) {
            result = funcNotSame(node.getVar());
        } else if (kind.equals("smaller")) {
            result = funcSmaller(node.getVar());
        } else if (kind.equals("overlap")) {
            result = funcOverlap(node.getVar());
        } else if (kind.equals("before")) {
            try {
                result = funcBefore(node.getVar());
            } catch (ParseException ex) {
                Logger.getLogger(BFunc.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else if (kind.equals("sz_loc_range")) {
            result = funcSZLocRange(node.getVar());
        } else if (kind.equals("sz_loc_dist")) {
            result = funcSZLocDist(node.getVar());
        } else if (kind.equals("sz_loc_dist_neq")) {
            result = funcSZLocDistNeq(node.getVar());
        } else if (kind.equals("sz_loc_close")) {
            result = funcSZLocClose(node.getVar());
        } else if (kind.equals("sz_spd_close")) {
            result = funcSZSpdClose(node.getVar());
        } else if (kind.equals("sf_late")) {
            result = funcSFLate(node.getVar());
        } else if (kind.equals("sf_worktime")) {
            result = funcSFWorktime(node.getVar());
        } else if (kind.equals("sf_close")) {
            result = funcSFClose(node.getVar());
        } else if (kind.equals("sf_far")) {
            result = funcSFFar(node.getVar());
        } else {
            System.out.println("incorrect function: " + kind);
            System.exit(1);
        }

        return result;
    }
    
    public boolean evaluateSymbolic(HashMap<String,Element> var) {
        boolean result = false;

        if (kind.equals("test")) {
            result = funcTest(var);
        } else if (kind.equals("true") || kind.equals("TRUE")) {
            result = funcTrue(var);
        } else if (kind.equals("equal") || kind.equals("Equal")) {
            result = funcEqual(var);
        } else if (kind.equals("not_equal")) {
            result = funcNotEqual(var);
        } else if (kind.equals("same") || kind.equals("Same")) {
            result = funcSame(var);
        } else if (kind.equals("not_same")) {
            result = funcNotSame(var);
        } else if (kind.equals("smaller")) {
            result = funcSmaller(var);
        } else if (kind.equals("overlap")) {
            result = funcOverlap(var);
        } else if (kind.equals("before")) {
            try {
                result = funcBefore(var);
            } catch (ParseException ex) {
                Logger.getLogger(BFunc.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else if (kind.equals("sz_loc_range")) {
            result = funcSZLocRange(var);
        } else if (kind.equals("sz_loc_dist")) {
            result = funcSZLocDist(var);
        } else if (kind.equals("sz_loc_dist_neq")) {
            result = funcSZLocDistNeq(var);
        } else if (kind.equals("sz_loc_close")) {
            result = funcSZLocClose(var);
        } else if (kind.equals("sz_spd_close")) {
            result = funcSZSpdClose(var);
        } else if (kind.equals("sf_late")) {
            result = funcSFLate(var);
        } else if (kind.equals("sf_worktime")) {
            result = funcSFWorktime(var);
        } else if (kind.equals("sf_close")) {
            result = funcSFClose(var);
        } else if (kind.equals("sf_far")) {
            result = funcSFFar(var);
        } else {
            System.out.println("incorrect function: " + kind);
            System.exit(1);
        }

        return result;
    }
    
    @Override
    public ArrayList<Link> linkGenerationEcc(HashMap<String,Context> contexts,RuntimeNode node) {
        ArrayList<Link> l = new ArrayList<Link>();
        return l;
    }

    @Override
    public boolean affect(ContextChange change) {
        return false;
    }
    
	@Override
	public boolean affect(String context) {
		return false;
	}

    @Override
    public boolean evaluatePcc(HashMap<String,Context> contexts,RuntimeNode node,ContextChange change) {
    	boolean result = evaluateEcc(contexts,node);
    	value = result;
        return value;
    }

    @Override
    public ArrayList<Link> linkGenerationPcc(HashMap<String,Context> contexts,RuntimeNode node,ContextChange change) {
        ArrayList<Link> l = new ArrayList<Link>();
        return l;
    }

    @Override
    public void setGoal(String goal) {
        goalLink = goal;
    }

	@Override
	public boolean evaluatePcc(HashMap<String,Context> contexts,RuntimeNode node, SameContextChange group) {
		boolean result = evaluateEcc(contexts,node);
    	value = result;
        return value;
	}

	@Override
	public ArrayList<Link> linkGenerationPcc(HashMap<String,Context> contexts,RuntimeNode node,
			SameContextChange group) {
		ArrayList<Link> l = new ArrayList<Link>();
        return l;
	}

	@Override
	public Formula createTreeNew(HashMap<String, Context> contexts) {
		// TODO Auto-generated method stub
		return this;
	}

	@Override
	public Formula formulaProcess(SameContextChange group,HashMap<String,Context> _contexts) {
		// TODO Auto-generated method stub
		 BFunc result = new BFunc(this.getKind());
         result.setValue(this.getValue());
         result.setLink(this.getLink());
         result.setParam(new HashMap(this.getParam()));
         return result;
	}

}
