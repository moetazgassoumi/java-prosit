public class Zoomanagement {

        public static void main(String[] args) {
                Animal lion = new Animal();
                lion.name = "Simba";
                lion.age = 8;
                lion.family = "Cats";
                lion.isMammal = true;

                Zoo myZoo = new Zoo("Wildlife Park", "Ariana",25);
                Zoo notMyZoo = new Zoo("WaterPark", "Siliana",25);


                Animal dog = new Animal("Canine", "Snoopy", 2, true);


                myZoo.displayZoo();

                System.out.println(myZoo);
                System.out.println(dog);


        }

}