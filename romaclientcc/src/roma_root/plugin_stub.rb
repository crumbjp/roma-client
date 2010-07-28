require 'zlib'
require 'digest/sha1'
require 'roma/async_process'

module Roma
  module CommandPlugin

    module PluginStorage
      include ::Roma::CommandPlugin

      # "set" means "store this data".
      # <command name> <key> <flags> <exptime> <bytes> [noreply]\r\n
      # <data block>\r\n
      def ev_set(s); set(s); end
      def ev_fset(s); set(s); end

      # get <key>*\r\n
      def ev_get(s); get(s); end

      # fget <key>
      def ev_fget(s); get(s); end

      # gets <key>*\r\n
      def ev_gets(s)
        # @@@@
        # Todo : not implements
      end

      # delete <key> [<time>] [noreply]\r\n
      def ev_delete(s); delete(s); end

      # fdelete <key> [<time>] [noreply]\r\n
      def ev_fdelete(s); delete(s); end

      # rdelete <key> <clock>
      def ev_rdelete(s)
        stub(s,{
               'NOT_FOUND'    => "NOT_FOUND\r\n",
               'DELETED'      => "DELETED\r\n",
               '_'            => "DELETED\r\n"
             })
      end

      # "add" means that "add a new data to a store"
      # <command name> <key> <flags> <exptime> <bytes> [noreply]\r\n
      # <data block>\r\n
      def ev_add(s); set(s); end
      def ev_fadd(s); set(s); end

      # "replace" means that "replace the previous data with a new one"
      # <command name> <key> <flags> <exptime> <bytes> [noreply]\r\n
      # <data block>\r\n
      def ev_replace(s); set(s); end
      def ev_freplace(s); set(s); end

      # "append" means that "append a new data to the previous one"
      # <command name> <key> <flags> <exptime> <bytes> [noreply]\r\n
      # <data block>\r\n
      def ev_append(s); set(s); end
      def ev_fappend(s); set(s); end

      # "prepend" means that "prepend a new data to the previous one"
      # <command name> <key> <flags> <exptime> <bytes> [noreply]\r\n
      # <data block>\r\n
      def ev_prepend(s); set(s); end
      def ev_fprepend(s); set(s); end


      # "cas" means that "store this data but only if no one else has updated since I last fetched it."
      # <command name> <key> <flags> <exptime> <bytes> <cas-id>[noreply]\r\n
      # <data block>\r\n
      def ev_cas(s); cas(s); end

      def ev_fcas(s); cas(s); end

      # incr <key> <value> [noreply]\r\n
      def ev_incr(s); incdec(s); end
      def ev_fincr(s); incdec(s); end

      # decr <key> <value> [noreply]\r\n
      def ev_decr(s); incdec(s); end
      def ev_fdecr(s); incdec(s); end

      # set_size_of_zredundant <n>
      def ev_set_size_of_zredundant(s)
        # @@@
        # Todo: what ?
      end

      # rset_size_of_zredundant <n>
      def ev_rset_size_of_zredundant(s)
        # @@@
        # Todo: what ?
      end


      def ev_alist_sized_insert(s)
        if ( s.length < 3 )
          send_data("SERVER_ERROR Parameters not enough !\r\n")
          return
        end
        v = read_bytes(s[3].to_i)
        read_bytes(2)
        stub(s,{
               'NOT_STORED'   => "NOT_STORED\r\n",
               'STORED'       => "STORED\r\n",
               '_'            => "STORED\r\n"
             },v)
      end


      def ev_alist_join(s)
        key,hname = s[1].split("\e")
        if ( s.length < 2 )
          send_data("SERVER_ERROR Parameters not enough !\r\n")
          return
        end
        v = read_bytes(s[2].to_i)
        read_bytes(2)
        stub(s,{
               'VALUE'        => "VALUE #{key} 0 11\r\nFOO,BAR,BAZ\r\nEND\r\n",
               'NULL'         => "END\r\n",
               '_'            => "END\r\n"
             },v)
      end

      def ev_alist_delete(s)
        if ( s.length < 2 )
          send_data("SERVER_ERROR Parameters not enough !\r\n")
          return
        end
        v = read_bytes(s[2].to_i)
        read_bytes(2)
        stub(s,{
               'NOT_FOUND'    => "NOT_FOUND\r\n",
               'NOT_DELETED'  => "NOT_DELETED\r\n",
               'DELETED'      => "DELETED\r\n",
               '_'            => "DELETED\r\n"
             },v)
      end

      def ev_alist_delete_at(s)
        stub(s,{
               'NOT_FOUND'    => "NOT_FOUND\r\n",
               'NOT_DELETED'  => "NOT_DELETED\r\n",
               'DELETED'      => "DELETED\r\n",
               '_'            => "DELETED\r\n"
             })
      end

      private

      def stub(s,rets,v="");
        key,hname = s[1].split("\e")
        key =~ /^([^_]+)_(.*)$/
        o = $1
        r = $2
        # o,r = key.split("_")
        rets['SERVER_ERROR'] = "SERVER_ERROR Some error occurred.\r\n"
        rets['ERROR'] = "ERROR\r\n"
        if ( o == 'CMD' ) 
          if ( rets[r] == nil )
            send_data(rets['_'])
          else
            send_data(rets[r])
          end
        elsif ( o == 'CMP2' ) 
          if ( s[2] == r )
            send_data(rets['_'])
          else
            send_data(rets['SERVER_ERROR'])
          end
        elsif ( o == 'CMP3' ) 
          if ( s[3] == r )
            send_data(rets['_'])
          else
            send_data(rets['SERVER_ERROR'])
          end
        elsif ( o == 'CMPV' )
          @log.error("***** #{v} #{r}")
          if ( v == r )
            send_data(rets['_'])
          else
            send_data(rets['SERVER_ERROR'])
          end
        elsif ( o == 'TO' ) 
          #sleep(r.to_i)
          #send_data(rets['_'])
        elsif ( o == 'CLOSE' )
          close_connection
        end
      end
      def set(s)
        if ( s.length < 4 )
          send_data("SERVER_ERROR Parameters not enough !\r\n")
          return
        end
        v = read_bytes(s[4].to_i)
        read_bytes(2)
        stub(s,{
               'NOT_STORED'   => "NOT_STORED\r\n",
               'STORED'       => "STORED\r\n",
               '_'            => "STORED\r\n"
             },v)
      end
      def get(s)
        key,hname = s[1].split("\e")
        stub(s,{
               'VALUE'        => "VALUE #{key} 0 6\r\nFOOBAR\r\nEND\r\n",
               'NULL'         => "END\r\n",
               '_'            => "END\r\n"
             })
      end

      def delete(s)
        stub(s,{
               'NOT_DELETED'  => "NOT_DELETED\r\n",
               'NOT_FOUND'    => "NOT_FOUND\r\n",
               'DELETED'      => "DELETED\r\n",
               '_'            => "DELETED\r\n"
             })
      end
      def cas(s)
        stub(s,{
               'NOT_STORED'   => "NOT_STORED\r\n",
               'NOT_FOUND'    => "NOT_FOUND\r\n",
               'EXISTS'       => "EXISTS\r\n",
               'STORED'       => "STORED\r\n",
               '_'            => "STORED\r\n"
             })
      end
      def incdec(s)
        stub(s,{
               'NOT_FOUND'    => "NOT_FOUND\r\n",
               '_'            => "10\r\n"
             })
      end

    end # module PluginStorage

  end # module CommandPlugin
end # module Roma