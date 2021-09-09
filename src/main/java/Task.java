public abstract class Task implements Runnable{
    //this is an abstract class that will create a constructor for each subclass of tasks

    public static Task createTask(String type, String roomID, String identity, String former, String content) {
        if (type.equals("identitychange")){
            return new Identity(former, identity);
        }else if (type.equals("join")){
            return new RChange(former, identity, roomID);
        }else if (type.equals("who")){
            return new RContent(identity, roomID);
        }else if ((type.equals("list")) || (type.equals("createroom"))){
            return new Rlist(roomID);
        }else if (type.equals("delete")){
            return new RDel(roomID);
        }else if (type.equals("message")){
            return new Mes(content);
        }else if (type.equals("quit")){
            return new Quit();
        }
        return null;
    }
}

