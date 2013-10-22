package typeusage.tests;

class Bar{

public String greet(String name){
        String greeting = new String("Hello");
        return greeting.concat(name);
}

public String abuse(String name){
        String curse = new String ("You have no class, ");
        return curse.concat(name);

}

private static void main(String[] args){
        String name = new String("Vivek Nallur");
        String f_name = name.substring(0,4);
        String l_name = name.substring(name.indexOf(' ') + 1);
        Bar foo = new Bar();
        System.out.println(foo.greet(name));
        System.out.println(foo.abuse(f_name));
}


} 